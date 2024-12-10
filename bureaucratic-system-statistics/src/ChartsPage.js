import { getFirestore, onSnapshot, collection } from "firebase/firestore";
import React, { useState, useEffect } from "react";
import MedianReadingTimeChart from "./components/Charts/MedianReadingTimeChart";
import MembershipsOverTimeChart from "./components/Charts/MembershipsOverTimeChart";
import RevenueByMembershipChart from "./components/Charts/RevenueByMembershipChart";
import FeesDistributionChart from "./components/Charts/FeesDistributionChart";
import HorizontalBarChart from "./components/Charts/HorizontalBarChart";
import { db } from "./firebaseConfig";

const ChartsPage = () => {
    const [readingTimeData, setReadingTimeData] = useState([]);
    const [membershipData, setMembershipData] = useState([]);
    const [revenueData, setRevenueData] = useState([]);
    const [feesDistribution, setFeesDistribution] = useState([]);
    const [usersFeesMemberships, setUsersFeesMemberships] = useState([]);
    const [topBooksBorrowed, setTopBooksBorrowed] = useState([]);
    const [topBooksKept, setTopBooksKept] = useState([]);
    const db = getFirestore();

    useEffect(() => {
        const fetchData = () => {
            // Fetch Borrow Data
            onSnapshot(collection(db, "borrows"), (snapshot) => {
                const borrowsData = snapshot.docs.map((doc) => doc.data());

                // Calculate Reading Time Per Book
                const readingTimes = borrowsData
                    .filter((borrow) => borrow.returnDate)
                    .map((borrow) => ({
                        bookTitle: borrow.bookTitle,
                        time: Math.ceil(
                            (new Date(borrow.returnDate) - new Date(borrow.borrowDate)) / (1000 * 60 * 60 * 24)
                        ),
                    }));

                const groupedTimes = readingTimes.reduce((acc, cur) => {
                    if (!acc[cur.bookTitle]) acc[cur.bookTitle] = [];
                    acc[cur.bookTitle].push(cur.time);
                    return acc;
                }, {});

                // Top 10 Longest Kept Books
                const topKeptBooks = Object.entries(groupedTimes)
                    .map(([bookTitle, times]) => ({
                        bookTitle,
                        maxTime: Math.max(...times),
                    }))
                    .sort((a, b) => b.maxTime - a.maxTime)
                    .slice(0, 10);
                setTopBooksKept(topKeptBooks);

                // Top 10 Most Borrowed Books
                const borrowCounts = borrowsData.reduce((acc, cur) => {
                    acc[cur.bookTitle] = (acc[cur.bookTitle] || 0) + 1;
                    return acc;
                }, {});

                const topBorrowedBooks = Object.entries(borrowCounts)
                    .map(([bookTitle, count]) => ({
                        bookTitle,
                        count,
                    }))
                    .sort((a, b) => b.count - a.count)
                    .slice(0, 10);
                setTopBooksBorrowed(topBorrowedBooks);

                setReadingTimeData(readingTimes);
            });

            // Fetch Membership and Fees Data
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

                onSnapshot(collection(db, "fees"), (feesSnapshot) => {
                    const feesData = feesSnapshot.docs.map((doc) => doc.data());
                    const revenueByMembership = feesData.reduce((acc, cur) => {
                        acc[cur.membershipId] = (acc[cur.membershipId] || 0) + cur.amount;
                        return acc;
                    }, {});

                    setRevenueData(
                        Object.entries(revenueByMembership).map(([membershipId, totalRevenue]) => ({
                            membershipId,
                            totalRevenue,
                        }))
                    );

                    const totalFees = feesData.reduce((acc, cur) => acc + cur.amount, 0);
                    setFeesDistribution(
                        Object.entries(revenueByMembership).map(([membershipId, amount]) => ({
                            membershipId,
                            percentage: ((amount / totalFees) * 100).toFixed(2),
                        }))
                    );

                    // Users, Fees, Memberships Summary
                    setUsersFeesMemberships([
                        { category: "Users", value: membershipsData.length },
                        { category: "Fees", value: totalFees },
                        { category: "Memberships", value: membershipsData.length },
                    ]);
                });
            });
        };

        fetchData();
    }, [db]);

    return (
        <div className="charts-container p-4 space-y-6">
            <h1 className="text-4xl font-bold text-center text-white mb-10">Admin Dashboard Charts</h1>
            <div className="space-y-10">
                {/* Users, Fees, Memberships */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Users, Fees, and Memberships</h2>
                    <HorizontalBarChart data={usersFeesMemberships} xKey="value" yKey="category" />
                </div>

                {/* Median Reading Time */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Median Reading Time</h2>
                    <MedianReadingTimeChart data={readingTimeData} />
                </div>

                {/* Top 10 Borrowed Books */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Top 10 Borrowed Books</h2>
                    <HorizontalBarChart data={topBooksBorrowed} xKey="count" yKey="bookTitle" />
                </div>

                {/* Top 10 Longest Kept Books */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Top 10 Longest Kept Books</h2>
                    <HorizontalBarChart data={topBooksKept} xKey="maxTime" yKey="bookTitle" />
                </div>

                {/* Memberships Over Time */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Memberships Over Time</h2>
                    <MembershipsOverTimeChart data={membershipData} />
                </div>

                {/* Revenue by Membership */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Revenue by Membership</h2>
                    <RevenueByMembershipChart data={revenueData} />
                </div>

                {/* Fees Distribution */}
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Fees Distribution</h2>
                    <FeesDistributionChart data={feesDistribution} />
                </div>
            </div>
        </div>
    );
};

export default ChartsPage;