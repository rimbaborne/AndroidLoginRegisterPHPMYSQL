<!DOCTYPE html>
<html>
<!-- 
*file ini digunakan untuk mengetes
proses penambahan data ke database dan login berhasil atau tidak.
*sehingga pada saat memnjalankan program menggunakan android, 
kesalahan tidak lagi pada databasenya.

#mempermudah troubleshooting
-->

<head>
	<title></title>
	<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>

<body>
	<div class="container">
	<br>
		<div class="col-md-4">
			<form action="register.php" method="post">
				nama
				<input type="text" name="name"><br> 
				email
				<input type="text" name="email"><br> 
				password
				<input type="text" name="password"><br>

				<button type="submit">User </button>
			</form>
		</div>
		<div class="col-md-4">
			<form action="login.php" method="post">
			    email
				<input type="text" name="email"><br>
				password
				<input type="text" name="password"><br>

				<button type="submit">Login </button>
			</form>
		</div>
	</body>

	</html>