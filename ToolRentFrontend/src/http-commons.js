import axios from "axios";

const payrollBackendServer = import.meta.env.VITE_PAYROLL_BACKEND_SERVER;
const payrollBackendHost = import.meta.env.VITE_PAYROLL_BACKEND_HOST;



export default axios.create({
    baseURL: `http://${payrollBackendServer}:${payrollBackendHost}`
})
