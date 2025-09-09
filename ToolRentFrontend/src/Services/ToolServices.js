import httpClient from "../http-commons"

const create = (data) =>{
    return httpClient.post("/api/Tools/", data)
}

const getAll = () => {
    return httpClient.get("/api/Tools/alltools")
}

const getid = id => {
    return httpClient.get(`/api/Tools/tool/${id}`)
}

const update = data => {
    return httpClient.put("/api/Tools/UpdateTool", data)
}

const deleteid = id  => {
    return httpClient.delete(`/api/Tools/${id}`)
}

const getinventory = data => {
    return httpClient.get("/api/Tools/inventory", data)
}

const unsuscribeTools = (idtool, iduser) =>{
    return httpClient.put("/api/Tools/", {params:{idtool,iduser}})
}

export default {getAll, create, getid, update, deleteid, getinventory, unsuscribeTools}