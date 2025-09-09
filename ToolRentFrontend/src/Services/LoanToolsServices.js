import httpClient from "../http-commons"

const create = data => {
    return httpClient.post("/api/LoanTools/")

}

const getid = id => {
    return httpClient.get(`/api/LoanTools/${id}`)

}

const updateTool = (iduser, idloantools) => {
    return httpClient.put("/api/LoanTools/return/",{params: {iduser, idloantools}}) 
}

const updateLoanTool = data => {
    return httpClient.put("/api/LoanTools/", data)
}

const deletes = id =>{
    return httpClient.delete(`/api/LoanTools/${id}`)
}

export default { create, getid, updateLoanTool, updateTool, deletes}