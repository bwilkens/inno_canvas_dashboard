export async function getUserData() {
  const response = await fetch("/api/v1/dashboard/users", {
    method: "GET",
    credentials: "include"
  });
  const unauthorizedStatus = 401;
  
  if (response.status === unauthorizedStatus) {
    return null;
  }
  if (!response.ok) {
    throw new Error("Could not fetch user data");
  }
  return await response.json();
}
