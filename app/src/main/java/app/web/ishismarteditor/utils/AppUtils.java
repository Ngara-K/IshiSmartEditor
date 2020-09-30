package app.web.ishismarteditor.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AppUtils {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    /*editors collection reference*/
    public static CollectionReference editorsCollection  = FirebaseFirestore.getInstance().collection("editors");
    /*morning tea collection reference*/
    public static CollectionReference morningTeaReference = FirebaseFirestore.getInstance()
            .collection("posts").document("morning_tea").collection("tea_collection");
}
