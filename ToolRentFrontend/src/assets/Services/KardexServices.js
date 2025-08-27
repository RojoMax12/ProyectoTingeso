import httpClient from "../http-commons";

//```usar cuando paso osea Id
const getAll = () => {
    return httpClient.get("/api/kardex/Allkardex");
}

const create = data => {
    return httpClient.post("/api/kardex/", data);
}

const deletekarex = id => {
    return httpClient.delete(`/api/kardex/${id}`);
}