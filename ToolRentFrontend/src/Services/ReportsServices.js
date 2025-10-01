import httpClient from "../http-commons";


const createLoanReport = () => {
    return httpClient.post("/api/report/ReportLoan");
};

const createClientLateReport = () => {
    return httpClient.post("/api/report/ReportClientLate");
};

const reportdate = (initdate, enddate) => {
    return httpClient.get(`/api/report/Reports/${initdate}/${enddate}`);
};

const getallReports = () => {
    return httpClient.get("/api/report/AllReports");
};

const getallReportsLoans = () => {
    return httpClient.get("/api/report/AllReportsLoan");
}

const getallReportsClientLate = () => {
    return httpClient.get("/api/report/AllReportClientLate");
}

export default { createLoanReport, reportdate, getallReports, getallReportsLoans, createClientLateReport, getallReportsClientLate };
