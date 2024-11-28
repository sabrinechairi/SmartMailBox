<?php
session_start();
include 'config.php'; 
$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';
    $email = $_POST['email'] ?? '';

    if (!empty($username) && !empty($password) && !empty($email)) {
        $stmt = $conn->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
        $stmt->bind_param("ss", $username, $email);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            echo json_encode(['code'=>'100','error' => 'Username or email already exists']);
        } else {
            $passwordHash = password_hash($password, PASSWORD_DEFAULT);

            $stmt = $conn->prepare("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            $stmt->bind_param("sss", $username, $passwordHash, $email);

            if ($stmt->execute()) {
                echo json_encode(['code'=>'200','success' => 'User registered successfully']);
            } else {
                echo json_encode(['code'=>'100','error' => 'Registration failed']);
            }

            $stmt->close();
        }
    } else {
        echo json_encode(['code'=>'100','error' => 'Username, password, and email are required']);
    }
}

$conn->close();
?>
