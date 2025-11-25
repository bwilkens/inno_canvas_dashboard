import { useParams } from "react-router-dom";
import { getDashboardUrl } from "../utils/dashboardUrl";

const DashboardPage = ({ userRole, userEmail }) => {
  const { instanceName } = useParams();
  const dashboardUrl = getDashboardUrl(instanceName, userRole, userEmail);

  return (
    <div style={{ height: "100vh" }}>
      <iframe
        src={dashboardUrl}
        title="Embedded Dashboard"
        style={{
          width: "100%",
          height: "100%",
          border: "none",
        }}
      />
    </div>
  );
};

export default DashboardPage;
