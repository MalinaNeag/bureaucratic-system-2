import React, { useState, useEffect } from "react";
import HorizontalBarChart from "../components/Charts/HorizontalBarChart";
import MembershipsOverTimeChart from "../components/Charts/MembershipsOverTimeChart";
import { db } from "../config/firebaseConfig";
import { collection, onSnapshot } from "firebase/firestore";

const UsersPage = () => {
    const [membershipData, setMembershipData] = useState([]);
    const [usersFeesMemberships, setUsersFeesMemberships] = useState([]);

    useEffect(() => {
        const fetchMembershipData = () => {
            onSnapshot(collection(db, "memberships"), (snapshot) => {
                const membershipsData = snapshot.docs.map((doc) => doc.data());
                const groupedByDate = membershipsData.reduce((acc, cur) => {
                    const date = new Date(cur.issueDate).toISOString().split("T")[0];
                    acc[date] = (acc[date] || 0) + 1;
                    return acc;
                }, {});

                setMembershipData(
                    Object.entries(groupedByDate).map(([date, count]) => ({
                        date,
                        count,
                    }))
                );

                const totalFees = membershipsData.reduce((acc, cur) => acc + (cur.fees || 0), 0);
                setUsersFeesMemberships([
                    { category: "Users", value: membershipsData.length },
                    { category: "Fees", value: totalFees },
                    { category: "Memberships", value: membershipsData.length },
                ]);
            });
        };

        fetchMembershipData();
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-center text-[#A87C5A] mb-6">Users Overview</h1>
            <div className="space-y-10">
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Users, Fees, and Memberships</h2>
                    <HorizontalBarChart data={usersFeesMemberships} xKey="value" yKey="category" />
                </div>
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Memberships Over Time</h2>
                    <MembershipsOverTimeChart data={membershipData} />
                </div>
            </div>
        </div>
    );
};

export default UsersPage;