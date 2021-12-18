package com.example.quotes


import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.type.DateTime
import com.squareup.okhttp.internal.DiskLruCache

import java.time.LocalDateTime
import javax.security.auth.callback.Callback

val db: FirebaseFirestore = FirebaseFirestore.getInstance()
var auth: FirebaseAuth = FirebaseAuth.getInstance()

val TAG:String = "FIREBASEDEBUG"
// date: Dec 13 2021


/*
 * Position of a user at certain time
 */

data class Position(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val time: Timestamp = Timestamp.now()
)

class UserData(
    val userid: String? = null,
    val name: String? = null,
    val lastPosition: Position? = null){

}

data class GroupData(
    var groupid: String? = null,
    var name: String? = null,
    var admins: List<String>? = null,
    var users: List<String>? = null
)


class UserManager
{
    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("users")


        @JvmStatic fun init(userid: String, email: String)
                /*
                 * Initialize a document of an user with ID: userid (Note: Use this function when the user has been created (sign up))
                 */
        {
            var document = UserManager.collection.document(userid)
            //var trackedGroups = listOf<String>()
            //var belongGroups = listOf<String>()
            //lastPosition = null
            //var positions = mapOf<String, Position>()

            var userData: UserData = UserData(userid = userid)

            document.set(userData)
                .addOnCompleteListener{
                    Log.d(TAG, "New user document was created! (${userData}")
                }
                .addOnFailureListener {  e -> Log.w(TAG, "Error while creating user document", e)}
        }

        @JvmStatic fun remove(user: FirebaseUser)
                /*
                 * Remove a user from users firebase collection
                 */
        {
            UserManager.collection.document(user.uid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "User was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        @JvmStatic fun leftGroup(user: FirebaseUser, groupid: String)
        {
            var group: GroupData? = null /*GroupManager.get(groupid)*/
            var userid: String = user.uid

            if(GroupManager.isUser(groupid, userid))
            {
                GroupManager.collection.document(groupid).update("users", FieldValue.arrayRemove(userid))
            }
        }

        @JvmStatic fun positions(user: FirebaseUser): List<Position>
                /*
                 * Return all the positions of a user
                 */
        {
            var userPositions: MutableList<Position> = mutableListOf<Position>()

            UserManager.collection.document(user.uid).collection("positions")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach { doc ->
                        var position = doc.toObject(Position::class.java)
                        userPositions.add(position!!)
                    }
                }

            return userPositions
        }

        @JvmStatic fun addPosition(user: FirebaseUser?, position: Position)
                /*
                 * Add a position of a user
                 */
        {
            if(user != null)
            {
                UserManager.collection.document(user.uid).update("lastPosition", position)
                UserManager.collection.document(user.uid).collection("positions").add(position)
                    .addOnSuccessListener { Log.d(TAG, "New position successfully added!") }
                    .addOnFailureListener {  e -> Log.w(TAG, "Error adding position document", e)}
            }
        }
        @JvmStatic fun lastPosition(user: FirebaseUser?): Position?
                /*
                 * Return the last position of a user
                 */
        {
            var userid = user!!.uid
            return  lastPosition(userid)

        }

        @JvmStatic fun lastPosition(userid: String): Position?
                /*
                 * Return the last position of a user
                 */
        {
            var position: Position? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener { documentSnapshot ->
                    var userData: UserData? = documentSnapshot.toObject(UserData::class.java)
                    Log.d(TAG, "User data: ${userData}")
                    position = userData!!.lastPosition
                    Log.d(TAG, "Last position: ${position}")
                }

            return position
        }

        @JvmStatic fun get(userid: String): UserData?
        {
            var userData: UserData? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener{ documentSnapshot ->
                    userData = documentSnapshot.toObject(UserData::class.java)
                }

            return userData
        }
    }
}


class GroupManager
/*
 * Group to tracking several users
 */ {

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("groups")

        @JvmStatic fun create(name: String): GroupData?
                /*
                 * Create a group (document) to DB
                 */
        {
            var userid: String? = auth.currentUser?.uid
            var admins: List<String> = listOf(userid!!)
            var users: List<String> = listOf()

            var group: GroupData? = GroupData(
                name = name,
                users = users,
                admins = admins
            )

            GroupManager.collection.add(group!!)
                .addOnSuccessListener { doc ->
                    var groupid: String = doc.id
                    Log.d(TAG, "Grupo ${groupid} agregado")
                }

            return group
        }

        @JvmStatic fun get(groupid: String): GroupData?
        {
            var data: GroupData? = null

            var doc = GroupManager.collection.document(groupid)
            doc.get()
                .addOnSuccessListener { documentSnapshot ->
                    data = documentSnapshot.toObject(GroupData::class.java)
                    Log.d(TAG, "Group ${groupid}: ${data}")
                }
            return data
        }


        @JvmStatic fun isAdmin(groupid: String, userid: String): Boolean
                /*
                 * Check if a user is an administrator of the group
                 */
        {
            var belong: Boolean = false

            var data: GroupData? = GroupManager.get(groupid)
            if(data != null)
            {
                for(admin in data.admins!!) {
                    if (admin == userid)
                    {
                        belong = true
                        break
                    }
                }
            }

            return belong

        }

        @JvmStatic fun isUser(groupid: String, userid: String): Boolean
                /*
                 * Check if a user belong to the a group
                 */
        {
            var belong: Boolean = false
            var groupData: GroupData? = null /*GroupManager.get(groupid)*/

            if(groupData != null)
            {
                for(user in groupData.admins!!) {
                    if (user == userid) {
                        belong = true
                        break
                    }
                }
            }

            return belong
        }

        @JvmStatic fun users(groupid: String): List<String>
                /*
                 * Return the tracked users of a group
                 */
        {
            var groupData: GroupData? = null /*GroupManager.get(groupid)*/

            return groupData!!.users!!
        }

        @JvmStatic fun addAdmin(groupid: String, userid: String)
        /*
         * Add an user to admins
         */
        {
            var userid: String = auth.currentUser!!.uid
            if(GroupManager.isAdmin(groupid, userid)) {
                GroupManager.collection.document(groupid).update("admins", FieldValue.arrayUnion(userid))
                Log.d(TAG, "Usuario ${userid} agreado a admins de grupo ${groupid}")
            }
            else
            {
                Log.d(TAG, "Solo administradores pueden agregar otros adminis")
            }
        }


        @JvmStatic fun addUser(groupid: String, userid: String)
        /*
         * Add an user to users
         */
        {
            var userid: String = auth.currentUser!!.uid
            if(GroupManager.isAdmin(groupid, userid)) {
                GroupManager.collection.document(groupid).update("users", FieldValue.arrayUnion(userid))
                Log.d(TAG, "Usuario ${userid} agreado al grupo ${groupid}")
            }
            else
            {
                Log.d(TAG, "Solo administradores pueden agregar otros usuarios")
            }
        }

        @JvmStatic fun remove(groupid: String)
                /*
                 * Remove a group (document) from groups firebase collection
                 */
        {
            GroupManager.collection.document(groupid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Group was successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        @JvmStatic fun lastPositions(groupid: String): MutableMap<String, Position>
                /*
                 * Return the last position of all the users of this group
                 */
        {
            var groupData: GroupData? = null /*GroupManager.get(groupid)*/
            var lastPositions: MutableMap<String, Position> = mutableMapOf()

            groupData!!.users?.forEach { userid ->
                var position: Position = UserManager.lastPosition(userid)!!
                lastPositions[userid] = position
            }

            return lastPositions
        }
    }
}
