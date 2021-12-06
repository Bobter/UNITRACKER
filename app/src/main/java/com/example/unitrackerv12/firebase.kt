package com.example.unitrackerv12

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.auth.User
import com.google.type.DateTime

import java.time.LocalDateTime

val db: FirebaseFirestore = FirebaseFirestore.getInstance()
var auth: FirebaseAuth = FirebaseAuth.getInstance()

val TAG:String = "FIREBASEDEBUG"


/*
 * Position of a user at certain time
 */

data class Position(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val time: Timestamp = Timestamp.now()
)

data class User(
    val userid: String,
    val name: String,
    val lastPosition: Position? = null
)

class UserManager
{
    /*
    var uid: String? = null
    var name: String? = null
    var email: String? = null // change to a more suitable data type
    var lastPosition: Position? = null
    var positions: Map<String, Position>? = null
    var trackedGroups: List<String>? = null // reference to tracked groups
    var belongGroups: List<String>? = null // reference to groups that this user belong
    private lateinit var document: DocumentReference  //refence to user document


    fun addPosition(position: Position)
    /*
     * Add a position of a user (REPLACED BY User.addPosition static method)
     */
    {
        this.lastPosition = position
        this.document.update("lastPosition", position)
        this.document.collection("positions").add(position)
            .addOnSuccessListener { Log.d(TAG, "New position successfully added!") }
            .addOnFailureListener {  e -> Log.w(TAG, "Error adding position document", e)}
    }
*/
    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("users")

        /*
        @JvmStatic fun getCurrentUser(): User?
        {
            var user: FirebaseUser = auth.currentUser

            if(user != null)
                return User(user)
            return null
        }

         */

        @JvmStatic fun init(userid: String, email: String)
                /*
                 * Init a document of an user with ID: userid (Note: Use this function when the user has been created (sign up))
                 */
        {
            var document = UserManager.collection.document(userid)
            var trackedGroups = listOf<String>()
            var belongGroups = listOf<String>()
            //lastPosition = null
            var positions = mapOf<String, Position>()

            document.set(
                mapOf(
                    "name" to null,
                    "email" to email,
                    "lastPosition" to null,
                )
            )
                .addOnCompleteListener{
                    Log.d(TAG, "New user document was created!")
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

        @JvmStatic fun positions(user: FirebaseUser): List<Position>
                /*
                 * Return all the positions of a user
                 */
        {
            var userPositions: List<Position> = listOf<Position>()

            // more stuff

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
            var position: Position? = null
            if(user != null)
            {
                UserManager.collection.document(user.uid).get()
                    .addOnSuccessListener{ doc ->
                        var data = doc.data?.get("lastPosition")
                        Log.d(TAG, "Data: ${data}")
                    }
            }

            return position
        }

        @JvmStatic fun lastPosition(userid: String): Position?
                /*
                 * Return the last position of a user
                 */
        {
            var position: Position? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener{ doc ->
                    var data = doc.data?.get("lastPosition")
                    Log.d(TAG, "Data: ${data}")
                }
            return position
        }

        @JvmStatic fun get(userid: String): User?
        {
            var user: User? = null
            UserManager.collection.document(userid).get()
                .addOnSuccessListener{ doc ->
                    var data = doc.data?.get("lastPosition")
                    Log.d(TAG, "Data: ${data}")
                    // user = SOMETING
                }

            return user
        }
    }
}

data class Group(
    var groupid: String,
    var name: String,
    var admins: List<String>? = null,
    var users: List<String>? = null
)

class GroupManager
/*
 * Group to tracking several users
 */ {

    /*
    var name: String? = null
    lateinit var groupid: String
    var admins: Set<String>? = null // reference to trackers
    var users: Set<String>? = null  // reference to tracked users
    var document: DocumentReference? = null

    constructor(groupid: String)
    {
        this.groupid = groupid
        GroupManager.collection.document(groupid).get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            }
        this.document = GroupManager.collection.document(groupid)
    }
    constructor(name:String, admins: Set<String>, users: Set<String>)
    {
        this.name = name
        this.admins = admins
        this.users = users

        var data = mapOf(
            name to this.name,
            admins to this.admins,
            users to this.users
        )

        Group.collection.add(data)
            .addOnSuccessListener { doc ->
                this.document = doc
            }
    }
    */

    /*
    fun addAdmin(userid: String)
            /*
             * Add an user to admins
             */
    {
        this.document?.update("admins", FieldValue.arrayUnion(userid))
    }

    fun removeAdmin(userid: String)
            /*
             * Remove the user with userid from the admins
             */
    {
        this.document?.update("admins", FieldValue.arrayRemove(userid))
    }

    fun addAUser(userid: String)
            /*
             * Add an user to users
             */
    {
        this.document?.update("users", FieldValue.arrayUnion(userid))
    }

    fun removeUser(userid: String)
            /*
             * Remove the user with userid from the users
             */
    {
        this.document?.update("users", FieldValue.arrayRemove(userid))
    }


    fun lastPositions(): Map<User, Position>
            /*
             * Return the last position of all the member of this group
             */
    {
        val lastUserPositions: Map<User, Position> = HashMap<User, Position>()

        this.document?.get()
            ?.addOnSuccessListener { document ->
                //SOMETHING
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return lastUserPositions
    }
    */

    companion object{
        @JvmField
        val collection: CollectionReference = db.collection("groups")

        @JvmStatic fun create(name: String, admins: List<String>? = null, users: List<String>? = null): Group?
                /*
                 * Create a group (document) to DB
                 */
        {
            var group: Group? = null
            GroupManager.collection.add(
                mapOf(
                    "name" to name,
                    "admins" to admins,
                    "users" to users
                )
            )
                .addOnSuccessListener { doc ->
                    var groupid: String = doc.id
                    group = Group(groupid, name, admins, users)
                }

            return group
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

        @JvmStatic fun lastPositions(group: Group): Map<User, Position>
                /*
                 * Return the last position of all the users of this group
                 */
        {
            var last_positions: Map<User, Position> = mapOf<User, Position>()
            for(userid in group.users!!)
            {
                var user: User? = UserManager.get(userid)
                if(user != null)
                {
                    //last_positions[user] = user.lastPosition
                }
            }

            return last_positions
        }
    }
}