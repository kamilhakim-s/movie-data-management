# movie-data-management
Movie Metadata management system deployed on Openshift with Postgresql database, Spring Boot backend, and EFK stack for monitoring and logging

# MongoDB/embedded_movies to PostgreSQL Database

The populate_db.py script in the script folder imports the Huggingface dataset "MongoDB/embedded_movies" into a normalized PostgreSQL database structure. It creates separate tables for movies, genres, countries, languages, and people (cast, directors, writers), with appropriate relationship tables between them.

## Features

- Creates a fully normalized database schema with multiple tables
- Handles relationships between entities (movies, genres, countries, etc.)
- Processes data in batches to manage memory usage
- Provides detailed progress reporting
- Handles various data types including arrays, nested objects, and timestamps

## Prerequisites

- Python 3.6+
- PostgreSQL database server
- Python packages:
  - `datasets` (Huggingface datasets library)
  - `psycopg2-binary` (PostgreSQL adapter for Python)
  - `numpy` (For array manipulation)

## Installation

1. Clone this repository or download the script file.

2. Install the required dependencies:
   ```bash
   pip install datasets psycopg2-binary numpy
   ```

3. Create a PostgreSQL database:
   ```bash
   createdb movie_database
   ```

## Configuration

Update the database connection parameters in the `main()` function to match your PostgreSQL setup:

```python
db_params = {
    "host": "localhost",
    "database": "your_database_name",  # Replace with your database name
    "user": "your_username",           # Replace with your PostgreSQL username
    "password": "your_password",       # Replace with your PostgreSQL password
    "port": "5432"                     # Default PostgreSQL port is 5432
}
```

## Usage

1. Run the script to create the schema and import the data:
   ```bash
   python populate_db.py
   ```

2. The script will:
   - Connect to your PostgreSQL database
   - Create the necessary tables if they don't exist
   - Download the Huggingface dataset (if not already cached)
   - Process the data in batches and insert it into your database
   - Display progress information

## Customization

You can adjust these parameters in the `main()` function:

- `batch_size`: Number of records to process in each batch (default: 10)
- `max_rows`: Maximum number of records to import (default: 50, set to `None` to import all records)

For example, to process all records in batches of 100:

```python
batch_size = 100
max_rows = None  # Process all records
```

## Database Schema

The script creates the following tables:

1. `movies`: Main table for movie data
2. `genres`: List of unique movie genres
3. `countries`: List of unique countries
4. `languages`: List of unique languages
5. `persons`: List of unique people (cast members, directors, writers)

And these relationship tables:

1. `movie_genres`: Links movies to their genres
2. `movie_countries`: Links movies to their countries
3. `movie_languages`: Links movies to their languages
4. `movie_cast`: Links movies to their cast members
5. `movie_directors`: Links movies to their directors
6. `movie_writers`: Links movies to their writers

## Troubleshooting

If you encounter errors:

1. Check your PostgreSQL connection details
2. Ensure the database exists and is accessible
3. Verify that you have the required Python packages installed
4. Check that the user has permissions to create tables in the database

## Example Queries

After importing the data, you can run queries like:

```sql
-- Get all movies with their genres
SELECT m.title, g.name AS genre
FROM movies m
JOIN movie_genres mg ON m.id = mg.movie_id
JOIN genres g ON mg.genre_id = g.id
ORDER BY m.title;

-- Find movies by director
SELECT m.title, m.year
FROM movies m
JOIN movie_directors md ON m.id = md.movie_id
JOIN persons p ON md.person_id = p.id
WHERE p.name = 'Christopher Nolan'
ORDER BY m.year;

-- Find the most common languages
SELECT l.name, COUNT(*) as movie_count
FROM languages l
JOIN movie_languages ml ON l.id = ml.language_id
GROUP BY l.name
ORDER BY movie_count DESC
LIMIT 10;
```