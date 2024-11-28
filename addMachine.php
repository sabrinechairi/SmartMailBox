<?php
session_start();
include 'config.php'; 

$response = [];

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['name'] ?? '';
    $machinecode = $_POST['machinecode'] ?? '';
    $location = $_POST['location'] ?? '';
    $session_token = $_POST['session_token'] ?? '';

    if (!empty($name) && !empty($machinecode) && !empty($location) && !empty($session_token)) {
        $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }
        $stmt = $conn->prepare("SELECT id FROM users WHERE session_token = ?");
        $stmt->bind_param("s", $session_token);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $stmt->bind_result($user_id);
            $stmt->fetch();
            $stmt = $conn->prepare("INSERT INTO machines (name, machinecode, userid, location) VALUES (?, ?, ?, ?)");
            $stmt->bind_param("ssis", $name, $machinecode, $user_id, $location);

            if ($stmt->execute()) {
                $response['status'] = 'success';
                $response['message'] = 'Machine added successfully';
            } else {
                $response['status'] = 'error';
                $response['message'] = 'Failed to add machine';
            }
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Invalid session token';
        }

        $stmt->close();
        $conn->close();
    } else {
        $response['status'] = 'error';
        $response['message'] = 'All fields are required';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Invalid request method';
}

echo json_encode($response);
?>
