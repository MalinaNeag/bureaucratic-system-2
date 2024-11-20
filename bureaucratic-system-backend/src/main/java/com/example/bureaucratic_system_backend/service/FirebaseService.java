package com.example.bureaucratic_system_backend.service;

import com.example.bureaucratic_system_backend.model.Book;
import com.example.bureaucratic_system_backend.model.Citizen;
import com.example.bureaucratic_system_backend.model.Fees;
import com.example.bureaucratic_system_backend.model.Membership;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class FirebaseService {

    private static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // ----------------------- Memberships -----------------------

    public static String getMembershipIdById(String citizenId) {
        try {
            ApiFuture<QuerySnapshot> query = getFirestore().collection("memberships")
                    .whereEqualTo("citizenId", citizenId).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            return documents.isEmpty() ? null : documents.get(0).getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addMembership(Membership newMembership) {
        Map<String, Object> membershipData = new HashMap<>();
        membershipData.put("id", newMembership.getMembershipNumber());
        membershipData.put("issueDate", newMembership.getIssueDate());
        membershipData.put("citizenId", newMembership.getCitizenId());

        try {
            getFirestore().collection("memberships").document(newMembership.getMembershipNumber()).set(membershipData).get();
            System.out.println("Membership added successfully: " + newMembership.getMembershipNumber());
        } catch (Exception e) {
            System.err.println("Error adding membership: " + e.getMessage());
        }
    }

    public void updateMembershipField(String membershipId, String fieldName, Object value) {
        updateField("memberships", membershipId, fieldName, value);
    }

    public void deleteMembership(String membershipId) {
        try {
            getFirestore().collection("memberships").document(membershipId).delete().get();
            System.out.println("Membership deleted successfully: " + membershipId);
        } catch (Exception e) {
            System.err.println("Error deleting membership: " + e.getMessage());
        }
    }

    // ----------------------- Books -----------------------

    public static boolean borrowBook(String bookId, String membershipId) {
        DocumentReference bookRef = getFirestore().collection("books").document(bookId);
        DocumentReference borrowRef = getFirestore().collection("borrows").document(bookId);

        Map<String, Object> bookUpdates = new HashMap<>();
        Map<String, Object> borrowData = new HashMap<>();
        bookUpdates.put("available", false);
        borrowData.put("membershipId", membershipId);
        borrowData.put("bookId", bookId);
        borrowData.put("borrowDate", LocalDate.now().toString());
        borrowData.put("dueDate", LocalDate.now().plusDays(30).toString());

        try {
            WriteResult bookResult = bookRef.update(bookUpdates).get();
            WriteResult borrowResult = borrowRef.set(borrowData).get();
            System.out.println("Book borrowed successfully: " + bookResult.getUpdateTime() + ", Borrow record created: " + borrowResult.getUpdateTime());
            return true;
        } catch (Exception e) {
            System.err.println("Error borrowing book: " + e.getMessage());
            return false;
        }
    }

    public static Book getBookByTitleAndAuthor(String title, String author) {
        try {
            ApiFuture<QuerySnapshot> query = getFirestore().collection("books")
                    .whereEqualTo("name", title)
                    .whereEqualTo("author", author)
                    .whereEqualTo("available", true).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            return documents.isEmpty() ? null : documents.get(0).toObject(Book.class);
        } catch (Exception e) {
            System.err.println("Error fetching book: " + e.getMessage());
            return null;
        }
    }

    public void addBook(Book book) {
        try {
            getFirestore().collection("books").document(book.getId()).set(book).get();
            System.out.println("Book added successfully: " + book.getName());
        } catch (Exception e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public void updateBookField(String bookId, String fieldName, Object value) {
        updateField("books", bookId, fieldName, value);
    }

    public void deleteBook(String bookId) {
        try {
            getFirestore().collection("books").document(bookId).delete().get();
            System.out.println("Book deleted successfully: " + bookId);
        } catch (Exception e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }

    public List<Book> getAllBooksFromFirestore() {
        List<Book> books = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> query = getFirestore().collection("books").get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                books.add(document.toObject(Book.class));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        return books;
    }

    // ----------------------- Citizens -----------------------

    public void addCitizen(Citizen citizen) {
        try {
            getFirestore().collection("citizens").document(citizen.getId()).set(citizen).get();
            System.out.println("Citizen added successfully: " + citizen.getName());
        } catch (Exception e) {
            System.err.println("Error adding citizen: " + e.getMessage());
        }
    }

    public void updateCitizenField(String citizenId, String fieldName, Object value) {
        updateField("citizens", citizenId, fieldName, value);
    }

    public void deleteCitizen(String citizenId) {
        try {
            getFirestore().collection("citizens").document(citizenId).delete().get();
            System.out.println("Citizen deleted successfully: " + citizenId);
        } catch (Exception e) {
            System.err.println("Error deleting citizen: " + e.getMessage());
        }
    }

    // ----------------------- Fees -----------------------

    public void addFee(Fees fee) {
        try {
            getFirestore().collection("fees").document(fee.getId()).set(fee).get();
            System.out.println("Fee added successfully: " + fee.getId());
        } catch (Exception e) {
            System.err.println("Error adding fee: " + e.getMessage());
        }
    }

    public void updateFee(String feeId, Fees updatedFee) {
        try {
            getFirestore().collection("fees").document(feeId).set(updatedFee).get();
            System.out.println("Fee updated successfully: " + feeId);
        } catch (Exception e) {
            System.err.println("Error updating fee: " + e.getMessage());
        }
    }

    public Fees getFeeByBorrowId(String borrowId) {
        try {
            ApiFuture<QuerySnapshot> query = getFirestore().collection("fees")
                    .whereEqualTo("borrowId", borrowId).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            return documents.isEmpty() ? null : documents.get(0).toObject(Fees.class);
        } catch (Exception e) {
            System.err.println("Error fetching fee: " + e.getMessage());
            return null;
        }
    }

    public void deleteFee(String feeId) {
        try {
            getFirestore().collection("fees").document(feeId).delete().get();
            System.out.println("Fee deleted successfully: " + feeId);
        } catch (Exception e) {
            System.err.println("Error deleting fee: " + e.getMessage());
        }
    }

    // ----------------------- General Field Update -----------------------

    public void updateField(String collectionName, String documentId, String fieldName, Object value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldName, value);

        try {
            getFirestore().collection(collectionName).document(documentId).update(updates).get();
            System.out.println(collectionName + " field '" + fieldName + "' updated successfully for ID: " + documentId);
        } catch (Exception e) {
            System.err.println("Error updating " + collectionName + " field '" + fieldName + "': " + e.getMessage());
        }
    }
}