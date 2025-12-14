import UserInfoSkeleton from "./UserInformationSkeleton.jsx";
import "../css/user-information.css";

const UserInfo = ({ data }) => {
  if (!data) {
    return (
      <div className="skeleton-wrapper">
        <UserInfoSkeleton />
      </div>
    );
  }

    return (
        <div id="user-info">
            <p>{data?.name}</p>
            <p>Email: {data?.email}</p>
            <p>Role: {data?.appRole}</p>
        </div>
    );
};

export default UserInfo;
