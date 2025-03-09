import psycopg2
from psycopg2.extras import execute_values
from datasets import load_dataset
import numpy as np
import time
import traceback
import uuid

def create_schema(conn):
    """
    Create a normalized database schema with separate tables for movies, genres, countries, etc.
    """
    cursor = conn.cursor()
    
    # Create tables SQL
    create_tables_sql = [
        """
        CREATE TABLE IF NOT EXISTS movies (
            id SERIAL PRIMARY KEY,
            movie_id TEXT UNIQUE,
            plot TEXT,
            runtime INTEGER,
            rated TEXT,
            num_mflix_comments INTEGER,
            poster TEXT,
            title TEXT,
            lastupdated TIMESTAMP,
            awards_wins INTEGER,
            awards_nominations INTEGER,
            awards_text TEXT,
            imdb_rating FLOAT,
            imdb_votes INTEGER,
            imdb_id TEXT,
            type TEXT,
            tomatoes_viewer_rating FLOAT,
            tomatoes_viewer_numreviews INTEGER,
            tomatoes_viewer_meter INTEGER,
            tomatoes_critic_rating FLOAT,
            tomatoes_critic_numreviews INTEGER,
            tomatoes_critic_meter INTEGER,
            tomatoes_dvd TIMESTAMP,
            tomatoes_production TEXT,
            plot_embedding FLOAT[],
            year INTEGER,
            fullplot TEXT,
            released TIMESTAMP,
            inserted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS genres (
            id SERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_genres (
            movie_id INTEGER REFERENCES movies(id),
            genre_id INTEGER REFERENCES genres(id),
            PRIMARY KEY (movie_id, genre_id)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS countries (
            id SERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_countries (
            movie_id INTEGER REFERENCES movies(id),
            country_id INTEGER REFERENCES countries(id),
            PRIMARY KEY (movie_id, country_id)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS languages (
            id SERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_languages (
            movie_id INTEGER REFERENCES movies(id),
            language_id INTEGER REFERENCES languages(id),
            PRIMARY KEY (movie_id, language_id)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS persons (
            id SERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_cast (
            movie_id INTEGER REFERENCES movies(id),
            person_id INTEGER REFERENCES persons(id),
            PRIMARY KEY (movie_id, person_id)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_directors (
            movie_id INTEGER REFERENCES movies(id),
            person_id INTEGER REFERENCES persons(id),
            PRIMARY KEY (movie_id, person_id)
        );
        """,
        """
        CREATE TABLE IF NOT EXISTS movie_writers (
            movie_id INTEGER REFERENCES movies(id),
            person_id INTEGER REFERENCES persons(id),
            PRIMARY KEY (movie_id, person_id)
        );
        """
    ]
    
    try:
        for sql in create_tables_sql:
            cursor.execute(sql)
        
        conn.commit()
        print("Database schema created successfully")
        return True
    except Exception as e:
        print(f"Error creating schema: {e}")
        print(traceback.format_exc())
        conn.rollback()
        return False

def extract_nested_value(obj, key_path):
    """
    Extract a value from a nested object using a path of keys
    Example: extract_nested_value(data, ['tomatoes', 'viewer', 'rating'])
    """
    if obj is None:
        return None
        
    current = obj
    for key in key_path:
        if isinstance(current, dict) and key in current:
            current = current[key]
        else:
            return None
    return current

def get_or_create_entity(conn, table_name, entity_name):
    """
    Get the ID of an entity from a table, or create it if it doesn't exist
    Returns the ID of the entity
    """
    if entity_name is None:
        return None
        
    cursor = conn.cursor()
    
    # Try to get the entity first
    select_sql = f"SELECT id FROM {table_name} WHERE name = %s"
    cursor.execute(select_sql, (entity_name,))
    result = cursor.fetchone()
    
    if result:
        return result[0]
    
    # Entity doesn't exist, create it
    insert_sql = f"INSERT INTO {table_name} (name) VALUES (%s) RETURNING id"
    cursor.execute(insert_sql, (entity_name,))
    conn.commit()
    
    return cursor.fetchone()[0]

def add_movie_relationship(conn, relationship_table, movie_id, entity_ids):
    """
    Add relationships between a movie and entities in a junction table
    """
    if not entity_ids:
        return
    
    cursor = conn.cursor()
    
    # Prepare values for insertion
    values = [(movie_id, entity_id) for entity_id in entity_ids if entity_id is not None]
    
    if not values:
        return
    
    # Map relationship tables to their corresponding ID column names
    relationship_id_map = {
        'movie_genres': 'genre_id',
        'movie_countries': 'country_id',
        'movie_languages': 'language_id',
        'movie_cast': 'person_id',
        'movie_directors': 'person_id',
        'movie_writers': 'person_id'
    }
    
    # Get the correct ID column name for this relationship table
    if relationship_table not in relationship_id_map:
        print(f"Unknown relationship table: {relationship_table}")
        return
    
    id_column = relationship_id_map[relationship_table]
    
    # SQL for inserting relationships
    insert_sql = f"INSERT INTO {relationship_table} (movie_id, {id_column}) VALUES %s ON CONFLICT DO NOTHING"
    
    try:
        execute_values(cursor, insert_sql, values)
        conn.commit()
    except Exception as e:
        print(f"Error adding relationships to {relationship_table}: {e}")
        print(f"Values: {values}")
        print(traceback.format_exc())
        conn.rollback()

def process_movie(conn, movie_data):
    """
    Process a movie record and insert it into the database with all relationships
    """
    cursor = conn.cursor()
    
    try:
        # Print movie title for debugging
        print(f"Processing movie: {movie_data.get('title')}")
        
        # Generate a unique ID
        movie_mongo_id = f"generated_{uuid.uuid4()}"
        
        # Handle imdb object
        imdb_rating = None
        imdb_votes = None
        imdb_id = None
        if 'imdb' in movie_data and isinstance(movie_data['imdb'], dict):
            imdb = movie_data['imdb']
            imdb_rating = imdb.get('rating')
            imdb_votes = imdb.get('votes')
            imdb_id = imdb.get('id')
        
        # Handle awards object
        awards_wins = None
        awards_nominations = None
        awards_text = None
        if 'awards' in movie_data and isinstance(movie_data['awards'], dict):
            awards = movie_data['awards']
            awards_wins = awards.get('wins')
            awards_nominations = awards.get('nominations')
            awards_text = awards.get('text')
        
        # Handle plot embedding (convert numpy array to Python list)
        plot_embedding = None
        if 'plot_embedding' in movie_data:
            plot_embedding = movie_data['plot_embedding']
            if isinstance(plot_embedding, np.ndarray):
                plot_embedding = plot_embedding.tolist()
            if not isinstance(plot_embedding, list):
                plot_embedding = [] if plot_embedding is None else [plot_embedding]
        
        # Insert the movie record
        insert_movie_sql = """
        INSERT INTO movies (
            movie_id, plot, runtime, rated, num_mflix_comments, poster, title, 
            fullplot, imdb_rating, imdb_votes, imdb_id, type, 
            awards_wins, awards_nominations, awards_text,
            plot_embedding, year
        ) VALUES (
            %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
        ) ON CONFLICT (movie_id) DO UPDATE SET
            plot = EXCLUDED.plot,
            title = EXCLUDED.title
        RETURNING id;
        """
        
        cursor.execute(insert_movie_sql, (
            movie_mongo_id, 
            movie_data.get('plot'),
            movie_data.get('runtime'),
            movie_data.get('rated'),
            movie_data.get('num_mflix_comments'),
            movie_data.get('poster'),
            movie_data.get('title'),
            movie_data.get('fullplot'),
            imdb_rating,
            imdb_votes,
            imdb_id,
            movie_data.get('type'),
            awards_wins,
            awards_nominations,
            awards_text,
            plot_embedding,
            None  # year is not in the sample data
        ))
        
        movie_id = cursor.fetchone()[0]
        conn.commit()
        
        # Process genres
        genres = movie_data.get('genres', [])
        if not isinstance(genres, list):
            genres = [genres] if genres else []
        
        genre_ids = []
        for genre in genres:
            if genre:  # Skip empty values
                genre_id = get_or_create_entity(conn, 'genres', genre)
                genre_ids.append(genre_id)
        
        add_movie_relationship(conn, 'movie_genres', movie_id, genre_ids)
        
        # Process countries
        countries = movie_data.get('countries', [])
        if not isinstance(countries, list):
            countries = [countries] if countries else []
        
        country_ids = []
        for country in countries:
            if country:  # Skip empty values
                country_id = get_or_create_entity(conn, 'countries', country)
                country_ids.append(country_id)
        
        add_movie_relationship(conn, 'movie_countries', movie_id, country_ids)
        
        # Process languages
        languages = movie_data.get('languages', [])
        if not isinstance(languages, list):
            languages = [languages] if languages else []
        
        language_ids = []
        for language in languages:
            if language:  # Skip empty values
                language_id = get_or_create_entity(conn, 'languages', language)
                language_ids.append(language_id)
        
        add_movie_relationship(conn, 'movie_languages', movie_id, language_ids)
        
        # Process cast
        cast = movie_data.get('cast', [])
        if not isinstance(cast, list):
            cast = [cast] if cast else []
        
        cast_ids = []
        for actor in cast:
            if actor:  # Skip empty values
                person_id = get_or_create_entity(conn, 'persons', actor)
                cast_ids.append(person_id)
        
        add_movie_relationship(conn, 'movie_cast', movie_id, cast_ids)
        
        # Process directors
        directors = movie_data.get('directors', [])
        if not isinstance(directors, list):
            directors = [directors] if directors else []
        
        director_ids = []
        for director in directors:
            if director:  # Skip empty values
                person_id = get_or_create_entity(conn, 'persons', director)
                director_ids.append(person_id)
        
        add_movie_relationship(conn, 'movie_directors', movie_id, director_ids)
        
        # Process writers
        writers = movie_data.get('writers', [])
        if not isinstance(writers, list):
            writers = [writers] if writers else []
        
        writer_ids = []
        for writer in writers:
            if writer:  # Skip empty values
                person_id = get_or_create_entity(conn, 'persons', writer)
                writer_ids.append(person_id)
        
        add_movie_relationship(conn, 'movie_writers', movie_id, writer_ids)
        
        print(f"Successfully processed movie: {movie_data.get('title')}")
        return True
        
    except Exception as e:
        print(f"Error processing movie {movie_data.get('title')}: {e}")
        print(traceback.format_exc())
        conn.rollback()
        return False

def main():
    # Database connection parameters - adjust these to match your PostgreSQL setup
    db_params = {
        "host": "localhost",
        "database": "movieproject_db",
        "user": "kamilhakims",
        "password": "db123",
        "port": "5432"
    }
    
    try:
        # Connect to the PostgreSQL database
        conn = psycopg2.connect(**db_params)
        print("Connected to PostgreSQL database")
        
        # Create schema if it doesn't exist
        if not create_schema(conn):
            print("Failed to create schema. Exiting.")
            return
        
        # Load the dataset
        print("Loading dataset from Huggingface...")
        try:
            ds = load_dataset("MongoDB/embedded_movies")
            print("Dataset loaded successfully!")
        except Exception as e:
            print(f"Error loading dataset: {e}")
            print(traceback.format_exc())
            return
        
        # Dataset info
        print(f"Dataset splits: {ds.keys()}")
        train_ds = ds['train']
        print(f"Training set size: {len(train_ds)} records")
        
        # Show the first record structure to understand the data
        first_record = train_ds[0]
        print("\nFirst record structure:")
        for key, value in first_record.items():
            value_type = type(value).__name__
            if isinstance(value, (list, dict)):
                print(f"{key} ({value_type}): {value}")
            else:
                print(f"{key} ({value_type})")
        
        # Variables for batch processing
        batch_size = 100
        total_rows = 0
        max_rows = None
        
        # Get total number of rows to process
        total_to_process = len(train_ds) if max_rows is None else min(len(train_ds), max_rows)
        print(f"\nProcessing {total_to_process} records...")
        
        # Process in batches
        for i in range(0, total_to_process, batch_size):
            # Get batch end index
            end_idx = min(i + batch_size, total_to_process)
            current_batch_size = end_idx - i
            
            print(f"\nBatch {i//batch_size + 1}: Processing records {i+1} to {end_idx}")
            
            # Process each movie in the batch
            successful_imports = 0
            for movie_idx in range(i, end_idx):
                movie_data = train_ds[movie_idx]
                if process_movie(conn, movie_data):
                    successful_imports += 1
            
            total_rows += successful_imports
            
            # Report progress
            progress = (total_rows / total_to_process) * 100
            print(f"Progress: {progress:.2f}% ({total_rows}/{total_to_process})")
            
        print(f"\nImport completed! Total movies imported: {total_rows}")
        
        # Close the connection
        conn.close()
        print("Database connection closed")
        
    except Exception as e:
        print(f"Error: {str(e)}")
        print(traceback.format_exc())

if __name__ == "__main__":
    main()