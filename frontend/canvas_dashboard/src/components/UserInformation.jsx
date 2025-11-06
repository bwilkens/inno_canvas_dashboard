import { user } from "../api/tempHardCodedUser";
import "../css/user-information.css";

const UserInfo = () => {
    return (
        <div id = "user-info">
            <p>{user.name}({user.studentCode})</p>
            <p>Email: {user.email}</p>
            <p>Datum: {user.date}</p>
        </div>
    );
};

export default UserInfo;
