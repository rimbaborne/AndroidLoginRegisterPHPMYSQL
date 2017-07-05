<?php

// membuat koneksi ke file lain untuk beberapa perintah lain
require_once 'include/function_loginreg.php';
$db = new function_loginreg();

// json response array
// memeriksa respon json
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['password'])) {

    // receiving the post params
    // menangkap data yang dikirimkan sebelumnya -> POST
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    // check if user is already existed with the same email
    // memeriksa apakah user telah terdaftar sebelumnya
    if ($db->isUserExisted($email)) {
        // user already existed
        // jika user telah terdaftar
        $response["error"] = TRUE;
        $response["error_msg"] = "Already registered with " . $email;
        echo json_encode($response);
    } else {
        // create a new user
        // jika user belum terdaftar maka membuat user baru
        $user = $db->storeUser($name, $email, $password);
        if ($user) {
            // user stored successfully
            // jika sukses, maka akan ditampilkan hasil pendaftaran
            $response["error"] = FALSE;
            $response["uid"] = $user["unique_id"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
            //ditampilkan dalam bentuk json
            echo json_encode($response);
        } else {
            // user failed to store
            // jika gagal didaftarkan
            $response["error"] = TRUE;
            $response["error_msg"] = "Error in registration!";
            echo json_encode($response);
        }
    }
} else {
    // jika ada kesalan dalam pendaftaran
    $response["error"] = TRUE;
    $response["error_msg"] = "Parameters is missing!";
    echo json_encode($response);
}
?>
