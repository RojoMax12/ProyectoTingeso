import httpClient from "../http-commons";


const createLoanReport = (data) => {
    return httpClient.post("/api/report/ReportLoan", data);
};

const reportdate = (initdate, enddate) => {
    return httpClient.get(`/api/report/Reports/${initdate}/${enddate}`);
};

const getallReports = () => {
    return httpClient.get("/api/report/AllReports");
};

export default { createLoanReport, reportdate, getallReports };
