<?php
// membuat koneksi ke file lain untuk beberapa perintah lain
require_once 'include/function_loginreg.php';
$db = new function_loginreg();


if (isset($_POST['email']) && isset($_POST['password'])) {

    // receiving the post params
    // menangkap data yang dikirimkan sebelumnya -> POST
    $email = $_POST['email'];
    $password = $_POST['password'];

    // get the user by email and password
    // menangkap data yang dikirimkan sebelumnya -> POST
    $user = $db->getUserByEmailAndPassword($email, $password);

    if ($user != false) {
        // use is found
        // jika user telah ditemukan dan cocok pada database, maka akan dimunculkan data user
        $response["error"] = FALSE;
        $response["uid"] = $user["unique_id"];
        $response["user"]["name"] = $user["name"];
        $response["user"]["email"] = $user["email"];
        $response["user"]["created_at"] = $user["created_at"];
        $response["user"]["updated_at"] = $user["updated_at"];

        // ditampilkan dalam bentuk json 
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        // jika user tidak ditemukan maka akan muncul pesan 
        $response["error"] = TRUE;
        $response["error_msg"] = "Login not matched. Please try again!";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    // jika tidak ada inputan untuk login
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
?>

