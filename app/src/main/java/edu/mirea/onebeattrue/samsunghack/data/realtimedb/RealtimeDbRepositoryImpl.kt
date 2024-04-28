package edu.mirea.onebeattrue.samsunghack.data.realtimedb

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.DbModel
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.RealtimeDbRepository
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.Timestamp
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RealtimeDbRepositoryImpl @Inject constructor(
    private val dbReference: DatabaseReference
) : RealtimeDbRepository {
    override suspend fun getPoints(): List<DbModel> {
        return suspendCancellableCoroutine { continuation ->
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val points = mutableListOf<DbModel>()

                    val childrenList = snapshot.children.toList()
                    for (i in 0..<childrenList.size - 1) {
                        val childSnapshot = childrenList[i]
                        val latitude =
                            childSnapshot.child("latitude").getValue(String::class.java)!!
                                .toDouble()
                        val longitude =
                            childSnapshot.child("longitude").getValue(String::class.java)!!
                                .toDouble()

                        val measurements = mutableListOf<Timestamp>()
                        val measurementsSnapshot = childSnapshot.child("measurements")
                        val measurementSnapshotList = measurementsSnapshot.children.toList()

                        for (j in 0..<measurementSnapshotList.size - 1) {
                            val measurementSnapshot = measurementSnapshotList[i]
                            val time = measurementSnapshot.child("time").getValue(String::class.java)
                                    .toString()
                            val value = measurementSnapshot.child("value").getValue(Double::class.java)
                                    .toString()
                            measurements.add(Timestamp(time, value))
                        }
                        points.add(DbModel(latitude, longitude, measurements))
                    }

                    Log.d("RealtimeDbRepositoryImpl", "$points")
                    continuation.resume(points)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
}