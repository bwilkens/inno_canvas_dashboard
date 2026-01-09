import UserInfoSkeleton from './UserInformationSkeleton.jsx';
import '../css/user-information.css';

const UserInfo = ({ data }) => {
    if (!data) {
        return (
            <div className="skeleton-wrapper">
                <UserInfoSkeleton />
            </div>
        );
    }

    return (
        <div id="user-container">
            <div id="user-card">
                <p>Naam: {data?.name}</p>
                <p>Email: {data?.email}</p>
                <p>Rol: {data?.appRole}</p>
            </div>
        </div>
    );
};

export default UserInfo;
