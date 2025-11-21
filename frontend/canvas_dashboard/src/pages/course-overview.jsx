import CardsGrid from "../components/CardGrid";
import UserInfo from "../components/UserInformation";
import useAuthCheck from "../hooks/useAuthCheck";

const CourseOverview = () => {
    useAuthCheck();

    return (
        <div>
            <UserInfo />
            <CardsGrid />
        </div>
    );
};

export default CourseOverview;