package app.web.ishismarteditor.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class firebaseUtils {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public static CollectionReference editorsCollection  = FirebaseFirestore.getInstance().collection("editors");
}
