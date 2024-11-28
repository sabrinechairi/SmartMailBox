<?php
include 'config.php'; 
function connectToDatabase() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}


$session_token = $_POST['session_token'];

$conn = connectToDatabase();
$sql = "SELECT id FROM users WHERE session_token = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $session_token);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if ($user) {
    $user_id = $user['id'];
    $machine_id = $_POST['id'];
    $name = $_POST['name'];
    $location = $_POST['location'];
    $sql = "UPDATE machines SET name = ?, location = ? WHERE id = ? AND userid = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssii", $name, $location, $machine_id, $user_id);
    $stmt->execute();

    if ($stmt->affected_rows > 0) {
        echo json_encode(["status" => "success", "message" => "Updated successfully"]);
    } else {
        echo json_encode(["status" => "failure", "message" => "Failed to update machine data"]);
    }
} else {
    echo json_encode(["status" => "failure", "message" => "Session error"]);
}
?>
