import CardsGrid from "../components/CardGrid";
import UserInfo from "../components/UserInformation";
import useAuthCheck from "../hooks/useAuthCheck";

const CourseOverview = () => {
    useAuthCheck();

    return (
        <div>
            <h1>Cursus Overzicht</h1>
            <UserInfo />
            <CardsGrid />
        </div>
    );
};

export default CourseOverview;