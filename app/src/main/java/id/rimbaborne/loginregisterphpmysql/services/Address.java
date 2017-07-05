package id.rimbaborne.loginregisterphpmysql.services;

/**
 * Created by Admin on 7/1/2017.
 * Yang mengatur alamat web yang berisikan API untuk menyimpan ke Database
 * yang pada dasarnya di webserver hanya memberikan umpan balik data JSON
 */

public class Address {
    // Server user login url
    public static String URL_LOGIN = "https://androidauth.000webhostapp.com/login.php";

    // Server user register url
    public static String URL_REGISTER = "https://androidauth.000webhostapp.com/register.php";
}
