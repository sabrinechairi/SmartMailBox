<?php
include 'config.php'; 
$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);


// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Assuming your session token is passed via POST from Android app
if (isset($_POST['session_token'])) {
    $session_token = $_POST['session_token'];

    // Query to check if session token exists in users table
    $sql = "SELECT * FROM users WHERE session_token = '$session_token'";
    $result = $conn->query($sql);

    if ($result->num_rows == 0) {
        // Session token does not exist in database, return JSON response indicating no session
        $response = array(
            "success" => false,
            "message" => "Session not found or expired"
        );
        echo json_encode($response);
    } else {
        // Session token exists, return JSON response indicating session is valid
        $response = array(
            "success" => true,
            "message" => "Session is valid"
        );
        echo json_encode($response);
    }
} else {
    // Handle case where session token is not provided
    $response = array(
        "success" => false,
        "message" => "Session token not provided"
    );
    echo json_encode($response);
}

$conn->close();
?>
