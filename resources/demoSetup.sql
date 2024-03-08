-- If we expect that we will want to do some data analysis from this table
-- or if we would expect it to grow massively, it could easily be partitioned
-- to increase performance, by things like release year, genre, etc.
CREATE TABLE IF NOT EXISTS movies (
    downvotes INTEGER,
    id INTEGER PRIMARY KEY,
    image_url TEXT,
    favorite_count INTEGER,
    release_year INTEGER,
    score INTEGER,
    title TEXT,
    upvotes INTEGER,
    details TEXT
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY
);

INSERT INTO users (id) VALUES (1);
INSERT INTO users (id) VALUES (2);
INSERT INTO users (id) VALUES (3);

CREATE TABLE IF NOT EXISTS user_votes (
    user_id INTEGER,
    movie_id INTEGER,
    vote INTEGER,
    PRIMARY KEY (user_id, movie_id)
);

CREATE TRIGGER update_votes_on_insert
    AFTER INSERT ON user_votes
BEGIN
    UPDATE movies
    SET upvotes = upvotes + CASE WHEN NEW.vote = 1 THEN 1 ELSE 0 END,
        downvotes = downvotes + CASE WHEN NEW.vote = -1 THEN 1 ELSE 0 END,
        score = (upvotes + CASE WHEN NEW.vote = 1 THEN 1 ELSE 0 END) -
                (downvotes + CASE WHEN NEW.vote = -1 THEN 1 ELSE 0 END)
    WHERE id = NEW.movie_id;
END;

CREATE TRIGGER update_votes_on_change
    AFTER UPDATE OF vote ON user_votes
    FOR EACH ROW
BEGIN
    UPDATE movies
    SET upvotes = upvotes + CASE WHEN NEW.vote = 1 AND OLD.vote = -1 THEN 1 WHEN NEW.vote = -1 AND OLD.vote = 1 THEN -1 ELSE 0 END,
        downvotes = downvotes + CASE WHEN NEW.vote = -1 AND OLD.vote = 1 THEN 1 WHEN NEW.vote = 1 AND OLD.vote = -1 THEN -1 ELSE 0 END,
        score = (upvotes + CASE WHEN NEW.vote = 1 AND OLD.vote = -1 THEN 1 WHEN NEW.vote = -1 AND OLD.vote = 1 THEN -1 ELSE 0 END) -
                (downvotes + CASE WHEN NEW.vote = -1 AND OLD.vote = 1 THEN 1 WHEN NEW.vote = 1 AND OLD.vote = -1 THEN -1 ELSE 0 END)
    WHERE id = NEW.movie_id;
END;

CREATE TRIGGER update_votes_on_delete
    AFTER DELETE ON user_votes
BEGIN
    UPDATE movies
    SET upvotes = upvotes - CASE WHEN OLD.vote = 1 THEN 1 ELSE 0 END,
        downvotes = downvotes + CASE WHEN OLD.vote = -1 THEN 1 ELSE 0 END,
        score = (upvotes - CASE WHEN OLD.vote = 1 THEN 1 ELSE 0 END) -
                (downvotes - CASE WHEN OLD.vote = -1 THEN 1 ELSE 0 END)
    WHERE id = OLD.movie_id;
END;

CREATE TABLE IF NOT EXISTS user_favorites (
      user_id INTEGER,
      movie_id INTEGER,
      PRIMARY KEY (user_id, movie_id)
);

CREATE TRIGGER increase_favorite_count
    AFTER INSERT ON user_favorites
BEGIN
    UPDATE movies SET favorite_count = favorite_count + 1 WHERE id = NEW.movie_id;
END;

CREATE TRIGGER decrease_favorite_count
    AFTER DELETE ON user_favorites
BEGIN
    UPDATE movies SET favorite_count = favorite_count - 1 WHERE id = OLD.movie_id;
END;