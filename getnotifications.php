<?php
session_start();
include 'config.php'; 

function connectToDatabase() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}

if (isset($_REQUEST['session_token'])) {
    $sessionToken = $_REQUEST['session_token'];
    
    $conn = connectToDatabase();

    $stmt = $conn->prepare("SELECT notifications.id , notifications.created_at as notTime ,machines.name as machName, notifications.readed as notR FROM notifications INNER JOIN machines ON notifications.machine_id = machines.machinecode WHERE machines.userid = (SELECT id FROM users WHERE session_token = ?);");
    $stmt->bind_param("s", $sessionToken);
    $stmt->execute();
    $result = $stmt->get_result();

    $notifications = array();
     $wheres='';
    while ($row = $result->fetch_assoc()) {
        $notifications[] = $row;
        $wheres.=$row['id'].",";
    }
    $wheres.="-1);";

    header('Content-Type: application/json');
    echo json_encode($notifications);

    $update_stmt = $conn->prepare("UPDATE notifications SET readed = 'yes' WHERE id IN (".$wheres);
    $update_stmt->execute();
    $update_stmt->close();
    

    $stmt->close();
    $conn->close();
} else {
    header('Content-Type: application/json');
    echo json_encode(array('error' => 'Session token not provided'));
}
?>
