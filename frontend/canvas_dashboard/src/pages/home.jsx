import '../css/home.css';
import HomeCardGrid from '../components/HomeCardGrid';

const HomePage = ({ userRole }) => {

    return (
        <div className="home-container">
            <div className="home-background">
                <h1>Home page</h1>
                <HomeCardGrid userRole={userRole} />
            </div>
        </div>
    );
};

export default HomePage;
