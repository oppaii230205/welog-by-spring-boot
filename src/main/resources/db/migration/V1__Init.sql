CREATE TABLE "users" (
                         "id" int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         "name" varchar(100),
                         "email" varchar(254) UNIQUE NOT NULL,
                         "photo" varchar(255),
                         "password" varchar(100),
                         "password_changed_at" timestamptz,
                         "password_reset_token" varchar,
                         "password_reset_expires" timestamptz,
                         "active" bool
);

CREATE TABLE "posts" (
                         "id" INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         "title" varchar(100),
                         "slug" varchar(100),
                         "content" text,
                         "excerpt" varchar(200),
                         "cover_image" varchar(255),
                         "author_id" integer NOT NULL,
                         "created_at" timestamptz
);

CREATE TABLE "tags" (
                        "id" INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        "name" varchar(100)
);

CREATE TABLE "posts_tags" (
                              "post_id" integer,
                              "tag_id" integer,
                              PRIMARY KEY ("post_id", "tag_id")
);

CREATE TABLE "comments" (
                            "id" int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            "content" text,
                            "post_id" integer NOT NULL,
                            "user_id" integer NOT NULL,
                            "created_at" timestamptz
);

CREATE TABLE "roles" (
                         "id" int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         "name" varchar(20)
);

CREATE TABLE "users_roles" (
                               "id" int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               "user_id" integer NOT NULL,
                               "role_id" integer NOT NULL
);

ALTER TABLE "posts" ADD CONSTRAINT "user_posts" FOREIGN KEY ("author_id") REFERENCES "users" ("id");

ALTER TABLE "comments" ADD CONSTRAINT "user_comments" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "comments" ADD CONSTRAINT "post_comments" FOREIGN KEY ("post_id") REFERENCES "posts" ("id");

ALTER TABLE "posts_tags" ADD FOREIGN KEY ("post_id") REFERENCES "posts" ("id");

ALTER TABLE "posts_tags" ADD FOREIGN KEY ("tag_id") REFERENCES "tags" ("id");

ALTER TABLE "users_roles" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "users_roles" ADD FOREIGN KEY ("role_id") REFERENCES "roles" ("id");

-- Insert sample users
INSERT INTO "users" ("name", "email", "photo", "password", "active")
VALUES
    ('Jonas Schmedtmann', 'admin@welog.io', 'user-1.jpg', 'test1234', true),
    ('Lourdes Browning', 'loulou@example.com', 'user-2.jpg', 'test1234', true),
    ('Sophie Louise Hart', 'sophie@example.com', 'user-3.jpg', 'test1234', true),
    ('Ayla Cornell', 'ayls@example.com', 'user-4.jpg', 'test1234', true),
    ('Leo Gillespie', 'leo@example.com', 'user-5.jpg', 'test1234', true),
    ('Jennifer Hardy', 'jennifer@example.com', 'user-6.jpg', 'test1234', true),
    ('Kate Morrison', 'kate@example.com', 'user-7.jpg', 'test1234', true),
    ('Eliana Stout', 'eliana@example.com', 'user-8.jpg', 'test1234', true),
    ('Cristian Vega', 'chris@example.com', 'user-9.jpg', 'test1234', true),
    ('Steve T. Scaife', 'steve@example.com', 'user-10.jpg', 'test1234', true),
    ('Aarav Lynn', 'aarav@example.com', 'user-11.jpg', 'test1234', true),
    ('Miyah Myles', 'miyah@example.com', 'user-12.jpg', 'test1234', true),
    ('Ben Hadley', 'ben@example.com', 'user-13.jpg', 'test1234', true),
    ('Laura Wilson', 'laura@example.com', 'user-14.jpg', 'test1234', true),
    ('Max Smith', 'max@example.com', 'user-15.jpg', 'test1234', true),
    ('Isabel Kirkland', 'isabel@example.com', 'user-16.jpg', 'test1234', true),
    ('Alexander Jones', 'alex@example.com', 'user-17.jpg', 'test1234', true),
    ('Eduardo Hernandez', 'edu@example.com', 'user-18.jpg', 'test1234', true),
    ('John Riley', 'john@example.com', 'user-19.jpg', 'test1234', true),
    ('Lisa Brown', 'lisa@example.com', 'user-20.jpg', 'test1234', true);

-- Insert sample posts
INSERT INTO "posts" ("title", "slug", "content", "excerpt", "cover_image", "author_id", "created_at")
VALUES
    ('Getting Started with Node.js', 'getting-started-with-nodejs', 'Node.js is a powerful JavaScript runtime built on Chrome''s V8 JavaScript engine. It allows you to build scalable network applications using JavaScript on the server-side. In this post, we''ll cover the basics of setting up a Node.js project, creating your first server, and understanding the event-driven architecture that makes Node.js so efficient for I/O-heavy applications.', 'Node.js is a powerful JavaScript runtime built on Chrome''s V8 JavaScript engine. It allows you to build scalable network applications using JavaScript on the server-side... + ...', 'nodejs-cover.jpg', 1, '2023-01-15T10:00:00'),
    ('MongoDB Best Practices', 'mongodb-best-practices', 'MongoDB is a popular NoSQL database that offers high performance, high availability, and easy scalability. In this article, we''ll discuss schema design patterns, indexing strategies, and query optimization techniques to get the most out of your MongoDB deployment. We''ll also cover common pitfalls to avoid when working with document databases.', 'MongoDB is a popular NoSQL database that offers high performance, high availability, and easy scalability. In this article, we''ll discuss schema design patterns... + ...', 'mongodb-cover.jpg', 2, '2023-02-20T14:30:00'),
    ('Building RESTful APIs with Express', 'building-restful-apis-with-express', 'Express.js is the most popular web framework for Node.js, and for good reason. It''s minimal, flexible, and provides a robust set of features for building web applications and APIs. In this tutorial, we''ll walk through creating a complete RESTful API with Express, including proper route organization, middleware usage, error handling, and authentication.', 'Express.js is the most popular web framework for Node.js, and for good reason. It''s minimal, flexible, and provides a robust set of features for building web applications... + ...', 'express-cover.jpg', 5, '2023-03-10T09:15:00'),
    ('Authentication in Node.js Applications', 'authentication-in-nodejs-applications', 'Security is crucial for any application, and authentication is the first line of defense. This post covers various authentication strategies for Node.js applications, including session-based authentication, JWT (JSON Web Tokens), and OAuth. We''ll implement each approach and discuss their pros and cons to help you choose the right solution for your project.', 'Security is crucial for any application, and authentication is the first line of defense. This post covers various authentication strategies for Node.js applications... + ...', 'auth-cover.jpg', 6, '2023-04-05T11:45:00'),
    ('Testing Node.js Applications', 'testing-nodejs-applications', 'Writing tests is an essential part of developing reliable applications. In this guide, we''ll explore different testing approaches for Node.js applications, including unit tests, integration tests, and end-to-end tests. We''ll use popular testing frameworks like Mocha and Jest, and learn how to mock dependencies and test asynchronous code effectively.', 'Writing tests is an essential part of developing reliable applications. In this guide, we''ll explore different testing approaches for Node.js applications... + ...', 'testing-cover.jpg', 7, '2023-05-12T13:20:00'),
    ('Deploying Node.js Applications', 'deploying-nodejs-applications', 'Once your application is ready, you need to deploy it to a production environment. This post compares different deployment options for Node.js applications, including traditional servers, containers (Docker), and serverless platforms. We''ll walk through deployment processes for each option and discuss considerations like scalability, cost, and maintenance.', 'Once your application is ready, you need to deploy it to a production environment. This post compares different deployment options for Node.js applications... + ...', 'deployment-cover.jpg', 8, '2023-06-18T15:10:00');

-- Tags
INSERT INTO "tags" ("name") VALUES
                                ('nodejs'),
                                ('backend'),
                                ('javascript'),
                                ('mongodb'),
                                ('database'),
                                ('nosql'),
                                ('express'),
                                ('api'),
                                ('authentication'),
                                ('security'),
                                ('jwt'),
                                ('testing'),
                                ('mocha'),
                                ('jest'),
                                ('deployment'),
                                ('docker'),
                                ('serverless');

-- posts_tags
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES
                                                   (1, 1), (1, 2), (1, 3),
                                                   (2, 4), (2, 5), (2, 6),
                                                   (3, 7), (3, 8), (3, 2),
                                                   (4, 9), (4, 10), (4, 11),
                                                   (5, 12), (5, 13), (5, 14),
                                                   (6, 15), (6, 16), (6, 17);

INSERT INTO "roles" ("name") VALUES
                                 ('ROLE_USER'),
                                 ('ROLE_ADMIN'),
                                 ('ROLE_SUPER_ADMIN');

INSERT INTO "users_roles" ("user_id", "role_id") VALUES
                                                     (1, 1),
                                                     (2, 1),
                                                     (3, 1),
                                                     (4, 1),
                                                     (5, 1),
                                                     (6, 1),
                                                     (7, 1),
                                                     (8, 1),
                                                     (9, 1),
                                                     (10, 1),
                                                     (11, 1),
                                                     (12, 1),
                                                     (13, 1),
                                                     (14, 1),
                                                     (15, 1),
                                                     (16, 1),
                                                     (17, 1),
                                                     (18, 1),
                                                     (19, 1),
                                                     (20, 1),
                                                     (1, 2),
                                                     (1, 3);


-- SELECT current_database();

--ALTER TABLE posts
--RENAME COLUMN author TO author_id

-- Updated hashed password
UPDATE users
SET password = '$2a$10$jQHbvEdTnxzroD/ndqAcx.ah1G9Ws1jQuogrTHlA0Ex5RasUrxWLu'

-- Drop database
--DROP DATABASE postgres WITH (FORCE);
