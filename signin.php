<?php
session_start();
include 'config.php'; 

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    if (!empty($username) && !empty($password)) {
        $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

        if ($conn->connect_error) {
            die(json_encode(['responseCode' => '500', 'message' => 'Connection failed: ' . $conn->connect_error]));
        }

        $stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $stmt->bind_result($user_id, $hashed_password);
            $stmt->fetch();

            if (password_verify($password, $hashed_password)) {
                $session_token = bin2hex(random_bytes(32));

                $stmt = $conn->prepare("UPDATE users SET session_token = ? WHERE id = ?");
                $stmt->bind_param("si", $session_token, $user_id);
                $stmt->execute();

                echo json_encode(['responseCode' => '112', 'message' => 'Authenticated successfully', 'session_token' => $session_token]);
            } else {
                echo json_encode(['responseCode' => '110', 'message' => 'Invalid credentials']);
            }
        } else {
            echo json_encode(['responseCode' => '113', 'message' => 'No such user found']);
        }

        $stmt->close();
        $conn->close();
    } else {
        echo json_encode(['responseCode' => '400', 'message' => 'Username and password required']);
    }
} else {
    echo json_encode(['responseCode' => '405', 'message' => 'Invalid request method']);
}
?>
