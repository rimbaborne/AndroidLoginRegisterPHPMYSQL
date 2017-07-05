<?php



class function_loginreg {

    private $conn;

    // constructor
    function __construct() {
        // mengkoneksi ke file lain
        require_once 'connect_db.php';

        // connecting to database
        // mengkoneksikan ke
        $db = new connect_db();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {

    }

    /**
     * Storing new user
     * returns user details
     * fungsi untuk mendaftarkan user
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt untuk menggadakan keamanan
        
        $tgl  = date("Y-m-d");
        $jam  = date("H");
        $jamok = $jam + 7;  //menyesuaikan zona waktu. jikalau tidak, bisa disetting di mysql
        $waktu = date("i:s");
        
        $date = $tgl." ".$jamok.":".$waktu;

        //perintah memsaukkan ke table users dan row
        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, ?)");

        //isi data dari variabel yang akan dimasukkan ke database
        // varibel -> ke symbol 's' -> ke symbol '?' (banyak symbol 's' sesuai dengan banyak variabel dan symbol '?')
        $stmt->bind_param("ssssss", $uuid, $name, $email, $encrypted_password, $salt, $date);

        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        // memriksa apakah berhasil didaftarkan
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();

            $stmt->close();

            return $user; 
        } else {
            return false;
        }
    }



    /**
     * Get user by email and password
     * Mengambil data email dan password untuk keperluan autentiksi
     */
    public function getUserByEmailAndPassword($email, $password) {
        // memanggil data yang sesuai dengan email
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()){ 
            //menyiapkan data yg diambil, fetch data
            $user = $stmt->get_result()->fetch_assoc();         
            $stmt->close();                                     
                                                              
            // verifying user password                                  
            // ferifikasi kecocokan password                            
            $salt = $user['salt'];                              
            $encrypted_password = $user['encrypted_password'];  
            $hash = $this->checkhashSSHA($salt, $password);     
                                                                      
            // check for password equality                              
            // jika password sesuai dengan database                     
            if ($encrypted_password == $hash) {                 
                // user authentication details are correct              
                // maka dapat diambil dari database         
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     * fungsi untuk memeriksa user sudah terdaftar atau belum
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed
            // jika user sudah terdaftar maka data yg dikembalikan true
            $stmt->close();
            return true;
        } else {
            // user not existed
            // jika user belum terdaftar maka data yg dikembalikan false
            $stmt->close();
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     * tambahan keamanan enkripsi password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     * fungsi untuk memeriksa enkripsi pada saat login
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>
