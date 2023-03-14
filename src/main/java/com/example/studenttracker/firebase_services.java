/*
package com.example.studenttracker;

class FirebaseServices {
 




public void addTask(String courseID,String taskName) {
Map<String, Object> docData = new HashMap<>();

// TODO add different fields here
docData.put("name",taskName);
docData.put("course_id",courseID);


db.collection("tasks").add(docData);


}



 public void getCourses() {
    return db.collection("courses").get();
 }


 public void getTasks(String courseID) {
    return db.collection("tasks").whereEqualTo("course_id", courseID).get();
 }


 public void listenToCourses() {
    db.collection("courses")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                                    // ----> HERE YOU WILL GET ALL THE DOCUMENT, WHEN EVER NEW DOCUMENT IS ADDED OR UPDATED!
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List docs = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    // HERE YOU WILL GET THE EACH DOC FROM QUERY DOCUMENT SNAPSHOT.
                    // if (doc.get("name") != null) {
                    //     cities.add(doc.getString("name"));
                    // }
                    Log.d(TAG, "All Courses: " + doc.getString("name"));
                }
                
            }
        });
 }


 
 public void listenToTasksList(String courseID) {
    db.collection("tasks").whereEqualTo("course_id",courseID)
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                                    // ----> HERE YOU WILL GET ALL THE DOCUMENT, WHEN EVER NEW DOCUMENT IS ADDED OR UPDATED!
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List docs = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    // HERE YOU WILL GET THE EACH DOC FROM QUERY DOCUMENT SNAPSHOT.
                    // if (doc.get("name") != null) {
                    //     cities.add(doc.getString("name"));
                    // }
                    Log.d(TAG, "All Tasks in that course: " + doc.getString("name"));
                }
                
            }
        });
 }




}*/
