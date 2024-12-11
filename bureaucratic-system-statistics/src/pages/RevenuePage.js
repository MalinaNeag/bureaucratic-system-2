import React, { useState, useEffect } from "react";
import RevenueByMembershipChart from "../components/Charts/RevenueByMembershipChart";
import FeesDistributionChart from "../components/Charts/FeesDistributionChart";
import { db } from "../config/firebaseConfig";
import { collection, onSnapshot } from "firebase/firestore";

const RevenuePage = () => {
    const [revenueData, setRevenueData] = useState([]);
    const [feesDistribution, setFeesDistribution] = useState([]);

    useEffect(() => {
        const fetchRevenueData = () => {
            onSnapshot(collection(db, "fees"), (snapshot) => {
                const feesData = snapshot.docs.map((doc) => doc.data());

                const revenueByMembership = feesData.reduce((acc, cur) => {
                    acc[cur.membershipId] = (acc[cur.membershipId] || 0) + cur.amount;
                    return acc;
                }, {});

                const totalFees = feesData.reduce((acc, cur) => acc + cur.amount, 0);

                setRevenueData(
                    Object.entries(revenueByMembership).map(([membershipId, totalRevenue]) => ({
                        membershipId,
                        totalRevenue,
                    }))
                );

                setFeesDistribution(
                    Object.entries(revenueByMembership).map(([membershipId, amount]) => ({
                        membershipId,
                        percentage: ((amount / totalFees) * 100).toFixed(2),
                    }))
                );
            });
        };

        fetchRevenueData();
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-center text-[#A87C5A] mb-6">Revenue Overview</h1>
            <div className="space-y-10">
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Revenue by Membership</h2>
                    <RevenueByMembershipChart data={revenueData} />
                </div>
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Fees Distribution</h2>
                    <FeesDistributionChart data={feesDistribution} />
                </div>
            </div>
        </div>
    );
};

export default RevenuePage;