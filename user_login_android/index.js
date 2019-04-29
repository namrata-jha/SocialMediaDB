/*
RESTFUL Services by NodeJS
Author: Namrata Jha
Update: 15.04.2019
*/

var crypto = require('crypto');
var uuid = require('uuid');
var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');

// Connect to MySQL
var conn = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'njha1999',
    database: 'demonodejs'
});

// Password Util
var generateRandomString = function(length){
    return crypto.randomBytes(Math.ceil(length/2))
        .toString('hex')            // Convert to hexa format
        .slice(0, length);          // Return required number of characters
}

var sha512 = function(password, salt){
    var hash = crypto.createHmac('sha512', salt);   // Use SHA512
    hash.update(password);
    var value = hash.digest('hex');
    return {
        salt: salt,
        passwordHash: value
    };

};

function saltHashPassword(userPassword){
    var salt = generateRandomString(16);    // Generate a random string with 16 chars to salt
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

function checkHashPassword(userPassword, salt){
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

var app = express();

app.use(bodyParser.json()); //Accept JSON Params
app.use(bodyParser.urlencoded({extended:true}));    //Accept URL Encoded Params

app.post('/register/', (req, res, next) => {
    var post_data = req.body;   // Get POST params

    var uid = uuid.v4();
    var plaint_password = post_data.password;
    // var plaint_password = "123456";
    var hash_data = saltHashPassword(plaint_password);
    var password = hash_data.passwordHash;
    var salt = hash_data.salt;

    var name = post_data.name;
    var email = post_data.email;
    var profile_pic = post_data.profile_pic;
    // var name = "Namrata";
    // var email = "njha1999@gmail.com";

    conn.query('SELECT * from users where email = ?', [email], function(err, result, fields){

        conn.on('error', function(err){
            console.log('[MYSQL ERROR]', err);
        });

        if(result && result.length){
            res.status(409).send();
        }
        else{
            conn.query('INSERT into `users`(`unique_id`, `name`, `email`, `encrypted_password`, `salt`, `profile_picture_url`) VALUES (?, ?, ?, ?, ?)'
            , [uid, name, email, password, salt, profile_pic], function(err, result, fields){
                conn.on('error', function(err){
                    console.log('[MYSQL ERROR]', err);
                });
    
                console.log(result);
                res.status(200);
                res.json('Register successful!');
    
            });
        }
    });

    
});

app.post('/login/', (req, res, next) => {
    var post_data = req.body;

    var user_password = post_data.password;
    var email = post_data.email;

    conn.query('SELECT * FROM users where email=?', [email], (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('error', err);
        });

        if(result && result.length){
            
            var salt = result[0].salt;      // If account exists extract salt
            var encrypted_password = result[0].encrypted_password;

            // Hashed password from login request with salt in Database
            var hashed_password = checkHashPassword(user_password, salt).passwordHash;

            if(hashed_password==encrypted_password){
                console.log(JSON.stringify(result[0]).toString());
                res.end(JSON.stringify(result[0]));     // If password matches return all data
            } else {
                res.status(400).send();       // Wrong Password
            }



        } else {
            // res.json(JSON.stringify('User does not exist'));
                res.status(404).send();        // User doesn't exists
        }
    });
});

app.post('/post/', (req, res, next) => {
    var post_data = req.body;

    var unique_id = post_data.unique_id;
    var imageUrl = post_data.post_image_url;
    var timestamp = post_data.post_timestamp;
    var caption = post_data.post_caption;

    console.log('POST /post ?, ?, ?, ?', [unique_id, imageUrl, timestamp, caption]);

        conn.query('INSERT into `posts` (`unique_id`, `post_timestamp`, `post_image_url`, `post_caption`) VALUES (?, ?, ?, ?)'
        , [unique_id, timestamp, imageUrl, caption], function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);

            res.status(200);
            res.json("Posted successfully");

        });


});

app.post('/comment/:posterId/:postTimestamp', (req, res, next) => {
    
    var posterId = req.params.posterId;
    var postTimestamp = req.params.postTimestamp;
    
    var post_data = req.body;

    var commenter_id = post_data.commenter_id;
    var comment = post_data.comment_text;
    var comment_timestamp = post_data.comment_timestamp;

    console.log('POST /comment');

        conn.query('INSERT into `comments` (`commenter_id`, `post_timestamp`, `poster_id`, `comment_text`, `comment_timestamp`) VALUES (?, ?, ?, ?,?)'
        , [commenter_id, postTimestamp, posterId, comment, comment_timestamp], function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);

            res.status(200);
            res.json("Posted successfully");

        });


});

app.get("/comments/:posterId/:postTimestamp", (req, res, next) => {
    console.log("/ GET comments");

    var posterId = req.params.posterId;
    var postTimestamp = req.params.postTimestamp;

    var query = "select `name`, profile_picture_url, comment_text, comment_timestamp from comments join users on commenter_id = unique_id where poster_id = '"
                + posterId + "' and post_timestamp = '"+ postTimestamp +"'";

    conn.query(query, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log("Error: " + err);
        if(result && result.length){
            res.end(JSON.stringify(result));
        }
    });
});


app.post('/like/:posterId/:postTimestamp', (req, res, next) => {
    var post_data = req.body;

    var poster_id = req.params.posterId;
    var timestamp = req.params.postTimestamp;
    var liker_id = post_data.liker_id;

    console.log("/POST like");


        conn.query('INSERT into `likes` (`liker_id`, `post_timestamp`, `poster_id`) VALUES (?, ?, ?)'
        , [liker_id, timestamp, poster_id], function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);
            res.status(200);
            res.json("Posted successfully");

        });


});

app.post('/share/:posterId/:postTimestamp', (req, res, next) => {
    var post_data = req.body;

    var poster_id = req.params.posterId;
    var timestamp = req.params.postTimestamp;
    var sharer_id = post_data.sharer_id;
    var share_timestamp = post_data.share_timestamp;

    console.log("/POST share");

    var selectQuery = "SELECT * from `posts` where `unique_id` = '"+poster_id+"' and `post_timestamp` = '"+timestamp+"'";

    conn.query(selectQuery, (err, result, fields) => {
        conn.on('error', function(err){
            console.log('Error 1: [MYSQL ERROR]', err);
        });

        console.log(err);

        // var postInsertQuery = ;

        conn.query("INSERT into `posts` (`unique_id`, `post_timestamp`, `post_image_url`, `post_caption`) VALUES (?,?,?,?) "
            , [sharer_id, share_timestamp, result[0].post_image_url, result[0].post_caption],
            function(err, result, fields){
            
                conn.on('error', function(err){
                    console.log('Error 2: [MYSQL ERROR]', err);
                });
    
                console.log(err);

                conn.query('INSERT into `shares` (`sharer_id`, `post_timestamp`, `poster_id`, `share_timestamp`) VALUES (?, ?, ?, ?)'
                    , [sharer_id, timestamp, poster_id, share_timestamp], function(err, result, fields){
            
                    conn.on('error', function(err){
                        console.log('Error 3: [MYSQL ERROR]', err);
                    });

                    console.log(err);
                    res.status(200);
                    res.json("Posted successfully");

                });
                
            });
        
    });
        
});

app.delete('/like/:posterId/:postTimestamp/:likerId', (req, res, next) => {
    var post_data = req.params;

    var poster_id = post_data.posterId;
    var timestamp = post_data.postTimestamp;
    var liker_id = post_data.likerId;

    var deleteQuery = "DELETE from `likes` where `liker_id` = '"+liker_id +"' and `post_timestamp` = '"+ timestamp + "' and `poster_id` = '" +poster_id + "'"
        
    
    console.log("/DELETE like");

        conn.query(deleteQuery,  function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);
            res.status(200);
            res.json("Deleted like successfully");

        });


});

app.patch('/post/:posterId/:postTimestamp', (req, res, next) => {
    var post_data = req.params;

    var poster_id = post_data.posterId;
    var timestamp = post_data.postTimestamp;

    var post_caption = req.body.post_caption;
    var post_pic_url = req.body.post_image_url;

    var updateQuery = "UPDATE `posts` " 
                     + "set `post_caption` = '"+ post_caption + "', `post_image_url` = '" + post_pic_url +"' "
                     + "where `post_timestamp` = '"+ timestamp + "' and `unique_id` = '" +poster_id + "'";

    console.log("/UPDATE post");

    conn.query(updateQuery,  function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);
            res.status(200);
            res.json("Updated post successfully");

        });
});

app.delete('/post/:posterId/:postTimestamp', (req, res, next) => {
    var post_data = req.params;

    var poster_id = post_data.posterId;
    var timestamp = post_data.postTimestamp;

    var deleteQuery = "DELETE from `posts` where `post_timestamp` = '"+ timestamp + "' and `unique_id` = '" +poster_id + "'";

    console.log("/DELETE post");

        conn.query(deleteQuery,  function(err, result, fields){
            
            conn.on('error', function(err){
                console.log('Error: [MYSQL ERROR]', err);
            });

            console.log(err);

            var deleteShareQuery = "DELETE from `shares` where `share_timestamp` = '"+ timestamp + "' and `sharer_id` = '" +poster_id + "'";

    
            conn.query(deleteShareQuery,  function(err, result, fields){
                
                conn.on('error', function(err){
                    console.log('Error: [MYSQL ERROR]', err);
                });
    
                console.log(err);
                res.status(200);
                res.json("Deleted post successfully");
    
            });

        });

        


});

app.get("/likes/:posterId/:postTimestamp", (req, res, next) => {
    console.log("/ GET likers");

    var posterId = req.params.posterId;
    var postTimestamp = req.params.postTimestamp;

    var query = "SELECT unique_id, `name`, email, profile_picture_url FROM users join likes on unique_id = liker_id and poster_id = '"
                + posterId + "' and post_timestamp = '"+ postTimestamp +"'";

    conn.query(query, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log("Error: " + err);
        if(result && result.length){
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/search/:keyword", (req, res, next) => {
    console.log("/ GET search results");

    var keyword = req.params.keyword;

    var query = "select * from users where lower(`name`) like '%" +keyword + "%' order by `name`";

    conn.query(query, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log("Error: " + err);
        if(result && result.length){
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/shares/:posterId/:postTimestamp", (req, res, next) => {
    console.log("/ GET likers");

    var posterId = req.params.posterId;
    var postTimestamp = req.params.postTimestamp;

    var query = "SELECT unique_id, `name`, email, profile_picture_url FROM users join shares on unique_id = sharer_id and poster_id = '"
                + posterId + "' and post_timestamp = '"+ postTimestamp +"'";

    conn.query(query, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log("Error: " + err);
        if(result && result.length){
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/user/:userId", (req, res, next) => {
    console.log("/ GET user");
    var user_id = req.params.userId;

    conn.query("SELECT unique_id, `name`, email, profile_picture_url FROM users WHERE `unique_id` = '"+user_id+"'", (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        if(result && result.length){
            res.status(200);
            res.end(JSON.stringify(result));
        }
    });
});


app.get("/posts/:userId", (req, res, next) => {
    console.log("/ GET posts");
    var id = req.params.userId;

    var getPostQuery = "with posts_likes(poster_id, poster_name, profile_picture_url, post_timestamp, post_image_url, post_caption, likes_count) "+
    "as (select posts.unique_id, `name`, profile_picture_url, posts.post_timestamp, post_image_url, post_caption, count(distinct liker_id) "+
     "from users " +
      "natural join posts "+ 
       "left join likes "+
        "on posts.unique_id = likes.poster_id and posts.post_timestamp = likes.post_timestamp "+
        "group by posts.unique_id, posts.post_timestamp "+
        "order by posts.post_timestamp desc), "+
    "likes_bool(poster_id, post_timestamp, like_bool) "+
    "as ( select unique_id, posts.post_timestamp, count(liker_id) "+ 
        "from posts "+
        "left join likes "+ 
            "on liker_id = '" + id + "' and  posts.unique_id = likes.poster_id " +
                "and posts.post_timestamp = likes.post_timestamp "+
        "group by unique_id, posts.post_timestamp) "+
        
    "select posts_table.poster_id, poster_name, profile_picture_url, posts_table.post_timestamp, post_image_url, post_caption, likes_count, like_bool " +
        ", count(commenter_id) as comments_count, count(distinct sharer_id) as share_count "+
    "from "+
    "(select posts_likes.poster_id, poster_name, profile_picture_url, posts_likes.post_timestamp, post_image_url, post_caption, likes_count, like_bool "+ 
        "from posts_likes, likes_bool "+
            "where posts_likes.poster_id = likes_bool.poster_id and posts_likes.post_timestamp = likes_bool.post_timestamp) posts_table "+
        "left join comments "+
            "on posts_table.poster_id = comments.poster_id and posts_table.post_timestamp = comments.post_timestamp "+
        "left join shares "+
            "on posts_table.poster_id = shares.poster_id and posts_table.post_timestamp = shares.post_timestamp "+
        "group by posts_table.poster_id, posts_table.post_timestamp "+
        "order by posts_table.post_timestamp desc ";

    conn.query(getPostQuery, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log(err);

        if(result && result.length){
            // console.log(result);
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/posts/trending/:userId", (req, res, next) => {
    console.log("/ GET trending posts");
    var id = req.params.userId;

    var getPostQuery = "with posts_likes(poster_id, poster_name, profile_picture_url, post_timestamp, post_image_url, post_caption, likes_count) "+
    "as (select posts.unique_id, `name`, profile_picture_url, posts.post_timestamp, post_image_url, post_caption, count(distinct liker_id) "+
     "from users " +
      "natural join posts "+ 
       "left join likes "+
        "on posts.unique_id = likes.poster_id and posts.post_timestamp = likes.post_timestamp "+
        "group by posts.unique_id, posts.post_timestamp "+
        "order by posts.post_timestamp desc), "+
    "likes_bool(poster_id, post_timestamp, like_bool) "+
    "as ( select unique_id, posts.post_timestamp, count(liker_id) "+ 
        "from posts "+
        "left join likes "+ 
            "on liker_id = '" + id + "' and  posts.unique_id = likes.poster_id " +
                "and posts.post_timestamp = likes.post_timestamp "+
        "group by unique_id, posts.post_timestamp) "+
        
    "select posts_table.poster_id, poster_name, profile_picture_url, posts_table.post_timestamp, post_image_url, post_caption, likes_count, like_bool " +
        ", count(commenter_id) as comments_count, count(distinct sharer_id) as share_count "+
    "from "+
    "(select posts_likes.poster_id, poster_name, profile_picture_url, posts_likes.post_timestamp, post_image_url, post_caption, likes_count, like_bool "+ 
        "from posts_likes, likes_bool "+
            "where posts_likes.poster_id = likes_bool.poster_id and posts_likes.post_timestamp = likes_bool.post_timestamp) posts_table "+
        "left join comments "+
            "on posts_table.poster_id = comments.poster_id and posts_table.post_timestamp = comments.post_timestamp "+
        "left join shares "+
            "on posts_table.poster_id = shares.poster_id and posts_table.post_timestamp = shares.post_timestamp "+
        "group by posts_table.poster_id, posts_table.post_timestamp "+
        "order by sum(likes_count+comments_count+share_count) desc ";

    conn.query(getPostQuery, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log(err);

        if(result && result.length){
            // console.log(result);
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/posts/user/:userId", (req, res, next) => {
    console.log("/ GET posts");
    var id = req.params.userId;

    var getPostQuery = "with posts_likes(poster_id, poster_name, profile_picture_url, post_timestamp, post_image_url, post_caption, likes_count) "+
    "as (select posts.unique_id, `name`, profile_picture_url, posts.post_timestamp, post_image_url, post_caption, count(distinct liker_id) "+
     "from users " +
      "natural join posts "+ 
       "left join likes "+
        "on posts.unique_id = likes.poster_id and posts.post_timestamp = likes.post_timestamp "+
        "group by posts.unique_id, posts.post_timestamp "+
        "order by posts.post_timestamp desc), "+
    "likes_bool(poster_id, post_timestamp, like_bool) "+
    "as ( select unique_id, posts.post_timestamp, count(liker_id) "+ 
        "from posts "+
        "left join likes "+ 
            "on liker_id = '" + id + "' and  posts.unique_id = likes.poster_id " +
                "and posts.post_timestamp = likes.post_timestamp "+
        "group by unique_id, posts.post_timestamp) "+
        
    "select posts_table.poster_id, poster_name, profile_picture_url, posts_table.post_timestamp, post_image_url, post_caption, likes_count, like_bool " +
        ", count(commenter_id) as comments_count, count(distinct sharer_id) as share_count "+
    "from "+
    "(select posts_likes.poster_id, poster_name, profile_picture_url, posts_likes.post_timestamp, post_image_url, post_caption, likes_count, like_bool "+ 
        "from posts_likes, likes_bool "+
            "where posts_likes.poster_id = likes_bool.poster_id and posts_likes.post_timestamp = likes_bool.post_timestamp) posts_table "+
        "left join comments "+
            "on posts_table.poster_id = comments.poster_id and posts_table.post_timestamp = comments.post_timestamp "+
        "left join shares "+
            "on posts_table.poster_id = shares.poster_id and posts_table.post_timestamp = shares.post_timestamp "+
        "where posts_table.poster_id = '"+id+"'" +
        "group by posts_table.poster_id, posts_table.post_timestamp "+
        "order by posts_table.post_timestamp desc ";

    conn.query(getPostQuery, (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.json('Register error: ', err);
        });

        console.log(err);

        if(result && result.length){
            // console.log(result);
            res.end(JSON.stringify(result));
        }
    });
});

app.get("/profileImage/:Id", (req, res, next) => {
    console.log("/ GET posts");
    var id = req.params.Id;
    conn.query('SELECT `profile_picture_url` from `users` where `unique_id` = ?', [id], (err, result, fields) => {

        conn.on('error', (err) => {
            console.log('[MYSQL ERROR]', err);
            res.status(500);
            res.json('Register error: ', err);

        });

        console.log(err);

        if(result && result.length){
            res.json(result[0].profile_picture_url);
        }
    });
});


// Start server
app.listen(4000, ()=> {
    console.log("Server started on port 3000");
});

