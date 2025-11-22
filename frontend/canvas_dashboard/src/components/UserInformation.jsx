import { useState, useEffect } from "react";
import { getUserData } from "../api/getUserData.js";
import UserInfoSkeleton from "./UserInformationSkeleton.jsx";
import "../css/user-information.css";

const UserInfo = () => {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const userData = await getUserData();
        setData(userData);
      } catch (err) {
        console.error("Error loading user data:", err);
        setError(err);
      }  finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="skeleton-wrapper">
        <UserInfoSkeleton />
      </div>
    );
  }
  
  if (error) return <div>Error loading user information.</div>;

    return (
        <div id="user-info">
            <p>{data?.name}</p>
            <p>Email: {data?.email}</p>
            <p>Role: {data?.role}</p>
        </div>
    );
};

export default UserInfo;
