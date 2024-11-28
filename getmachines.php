<?php
session_start();
include 'config.php'; 

// Function to establish a database connection
function connectToDatabase() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}

// Check if session token is provided
if (isset($_REQUEST['session_token'])) {
    $sessionToken = $_REQUEST['session_token'];
    
    $conn = connectToDatabase();

    $stmt = $conn->prepare("SELECT id, name, location, created_at FROM machines WHERE userid = (SELECT id FROM users WHERE session_token = ?)");
    $stmt->bind_param("s", $sessionToken);
    $stmt->execute();
    $result = $stmt->get_result();

    // Fetch result as JSON array
    $machines = array();
    while ($row = $result->fetch_assoc()) {
        $machines[] = $row;
    }

    // Close database connection
    $stmt->close();
    $conn->close();

    // Output JSON response
    header('Content-Type: application/json');
    echo json_encode($machines);
} else {
    // Session token not provided
    header('Content-Type: application/json');
    echo json_encode(array('error' => 'Session token not provided'));
}
?>
