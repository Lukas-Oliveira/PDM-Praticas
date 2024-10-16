package com.weatherapp.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier)
{
    var username by rememberSaveable { mutableStateOf(value = "") }
    var email by rememberSaveable { mutableStateOf(value = "") }
    var password by rememberSaveable { mutableStateOf(value = "") }
    var passwordConfirm by rememberSaveable { mutableStateOf(value = "") }

    var activity = LocalContext.current as? Activity
    val firebaseDatabase = remember { FBDatabase() }

    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Cadastrar Usuário",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.size(20.dp))

        DataField(
            value = username,
            onChange = { username = it },
            label = "Digite seu Nome",
            modifier = modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.size(12.dp)
        )
        DataField(
            value = email,
            onChange = { email = it },
            label = "Digite o seu e-mail",
            modifier = modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.size(12.dp)
        )
        PasswordField(
            value = password,
            onChange = { password = it },
            label = "Digite a senha",
            modifier = modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.size(12.dp)
        )
        PasswordField(
            value = passwordConfirm,
            onChange = { passwordConfirm = it },
            label = "Confirme a senha",
            modifier = modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(20.dp))
        Row(
            modifier = Modifier
        ) {
            Button(onClick = { activity?.finish() }){ Text(text = "Voltar") }
            Spacer(modifier = Modifier.size(5.dp))
            
            Button(
                onClick = {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity!!) { task ->
                            if (task.isSuccessful) {

                                firebaseDatabase.register(User(username, email))
                                Firebase.auth.signInWithEmailAndPassword(email, password)

                                username = ""
                                email = ""
                                password = ""
                                passwordConfirm = ""

                                Toast.makeText(activity, "Cadastrado com Sucesso!",Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "O Cadastro Falhou!", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                enabled = username.isNotEmpty() &&
                          email.isNotEmpty()    &&
                          password.isNotEmpty() &&
                          password.equals(passwordConfirm)
            ) {
                Text(text = "Cadastrar")
            }
            Spacer(modifier = Modifier.size(5.dp))
            
            Button(
                onClick = {
                    username = ""
                    email = ""
                    password = ""
                    passwordConfirm = ""
                },
                enabled = username.isNotEmpty() ||
                          email.isNotEmpty()    ||
                          password.isNotEmpty() ||
                          passwordConfirm.isNotEmpty()
            ) {
                Text(text = "Limpar")
            }
        }
    }
}