// Base url of the container TODO change
const BASE_URL = import.meta.env.VITE_DASHBOARD_BASE_URL || "http://localhost:5000";

export const getDashboardUrl = (instanceName, role) => {
  console.log(instanceName, role);
  if (role === "ADMIN" || role === "TEACHER") {
    return `${BASE_URL}/${instanceName}/dashboard_${instanceName}/index.html`;
  } else if (role === "STUDENT") {
    return `${BASE_URL}/${instanceName}/index.html`;
  } else {
    return `${BASE_URL}/404.html`;
  }
};
