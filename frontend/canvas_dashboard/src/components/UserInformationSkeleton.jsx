import React from "react";
import "../css/skeleton.css";

const UserInfoSkeleton = () => {
  return (
    <div id="user-info" className="skeleton-card">
      <div className="skeleton skeleton-title" style={{ width: "150px" }}></div>
      <div className="skeleton skeleton-text" style={{ width: "200px" }}></div>
      <div className="skeleton skeleton-text" style={{ width: "100px" }}></div>
    </div>
  );
};

export default UserInfoSkeleton;
