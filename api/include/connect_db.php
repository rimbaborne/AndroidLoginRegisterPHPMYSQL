<?php
class connect_db {
    private $conn;

    // Connecting to database
    // Koneksi kedatabase
    public function connect() {
        require_once 'include/database.php';

        // Connecting to mysql database
        // Koneksi ke database MySQL
        $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

        // return database handler
        return $this->conn;
    }
}

?>

