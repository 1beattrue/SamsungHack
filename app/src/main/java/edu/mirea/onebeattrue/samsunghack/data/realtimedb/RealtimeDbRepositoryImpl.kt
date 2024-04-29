package edu.mirea.onebeattrue.samsunghack.data.realtimedb

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.Point
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.RealtimeDbRepository
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.Timestamp
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RealtimeDbRepositoryImpl @Inject constructor(
    private val dbReference: DatabaseReference
) : RealtimeDbRepository {

    override suspend fun getPoints(): List<Point> {
        return suspendCancellableCoroutine { continuation ->
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val points = mutableListOf<Point>()

                    for (childSnapshot in snapshot.children) {
                        val latitude =
                            childSnapshot.child("latitude").getValue(String::class.java)!!
                                .toDouble()
                        val longitude =
                            childSnapshot.child("longitude").getValue(String::class.java)!!
                                .toDouble()

                        points.add(Point(childSnapshot.key.toString(), latitude, longitude))
                    }

                    continuation.resume(points)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    override suspend fun getTimestamps(key: String): List<Timestamp> {
        return suspendCancellableCoroutine { continuation ->
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timestamps = mutableListOf<Timestamp>()
                    if (snapshot.hasChild(key)) {
                        val timestampsSnapshot = snapshot.child(key).child("measurements")
                        timestampsSnapshot.children.forEachIndexed { index, dataSnapshot ->
                            val time =
                                dataSnapshot.child("time").getValue(String::class.java)
                                    .formattedTime()
                            val value =
                                dataSnapshot.child("value").getValue(Double::class.java)
                                    .formattedValue()
                            timestamps.add(Timestamp(index, time, value))
                        }
                    }
                    Log.d("RealtimeDbRepositoryImpl", "$timestamps")
                    continuation.resume(timestamps)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    private fun <T> T.formattedValue(): String {
        return "%.2f".format(this)
    }

    private fun <T> T.formattedTime(): String {
        val inputFormat: SimpleDateFormat = if (this.toString().contains("-")) {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        } else {
            SimpleDateFormat("dd.MM.yyyy H:mm", Locale.getDefault())
        }

        val outputFormat = SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(this.toString())
        return date?.let { outputFormat.format(it) } ?: "Нет информации о времени"
    }
}