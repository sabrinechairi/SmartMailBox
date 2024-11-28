<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "mailbox";
include 'config.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if(isset($_GET['mc'])) {
    $machine_id = $_GET['mc'];

    $stmt = $conn->prepare("INSERT INTO notifications (machine_id) VALUES (?)");
    $stmt->bind_param("s", $machine_id);

    if ($stmt->execute()) {
        echo "New notification inserted successfully";
    } else {
        echo "Error: " . $stmt->error;
    }

    $stmt->close();
} else {
    echo "Error: 'mc' parameter is missing";
}

$conn->close();
?>
