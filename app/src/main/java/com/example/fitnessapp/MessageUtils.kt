package com.example.fitnessapp

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable

fun sendMessageToPhone(context: Context, path: String, message: ByteArray) {

    val nodeClient = Wearable.getNodeClient(context)
    nodeClient.connectedNodes.addOnSuccessListener { nodes ->
        for (node in nodes) {
            println(node.displayName)
            Wearable.getMessageClient(context)
                .sendMessage(node.id, path, message)
                .addOnSuccessListener {
                    Log.d("Wear", "Message sent successfully")
                }
                .addOnFailureListener {
                    Log.e("Wear", "Message failed: ${it.message}")
                }
        }
    }
}
