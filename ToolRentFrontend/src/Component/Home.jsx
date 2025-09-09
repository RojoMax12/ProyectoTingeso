import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ThemeProvider, CssBaseline, AppBar, Toolbar, Typography, Stack, Avatar, Button, Container, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, TextField, IconButton } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import AddBoxIcon from "@mui/icons-material/AddBox";
import MenuIcon from "@mui/icons-material/Menu";
import { createTheme } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";

const theme = createTheme();

const Home = () => {
  const navigate = useNavigate();
  const [rut, setRut] = useState("");
  const [drawerOpen, setDrawerOpen] = useState(false); // Estado para mostrar/ocultar la barra lateral
  const name = "Usuario";
  const photo = "";
  const {keycloak, setKeycloak} = useKeycloak();


  const handleLogout = () => { 
    alert("Sesión cerrada");
    keycloak.logout();
   };
  const formatRut = (value) => value;

  const sidebarOptions = [
    { text: "Inicio", icon: <HomeIcon />, path: "/" },
    { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
    { text: "Agregar Herramienta", icon: <AddBoxIcon />, path: "/AddTools" }
  ];

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />

      {/* HEADER FIJO ARRIBA */}
      <AppBar position="fixed"
        sx={{
          backgroundColor: "rgba(255, 94, 0, 1)",
          left: 0,
          width: "100%"
        }}>
        <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
          {/* Botón para abrir la barra lateral */}
          <IconButton
            color="inherit"
            edge="start"
            onClick={() => setDrawerOpen(true)}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h4" sx={{ fontWeight: "bold" }}>
            Tool Rent
          </Typography>
          <Stack direction="row" spacing={2} alignItems="center">
            <Avatar src={photo} alt="Usuario" />
            <Typography variant="subtitle1">{name}</Typography>
            <Button
              variant="contained"
              onClick={handleLogout}
              sx={{
                backgroundColor: "#fff",
                color: "#000",
                "&:hover": { backgroundColor: "#b49e99" }
              }}>
              Cerrar sesión
            </Button>
          </Stack>
        </Toolbar>
      </AppBar>

      {/* Barra lateral temporal */}
      <Drawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        variant="temporary"
        sx={{
          [`& .MuiDrawer-paper`]: { width: 220, boxSizing: "border-box", backgroundColor: "#fff8f0" }
        }}
      >
        <List>
          {sidebarOptions.map((option) => (
            <ListItem key={option.text} disablePadding>
              <ListItemButton onClick={() => { navigate(option.path); setDrawerOpen(false); }}>
                <ListItemIcon>{option.icon}</ListItemIcon>
                <ListItemText primary={option.text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Drawer>

      {/* CONTENIDO CENTRAL */}
      <Container
        sx={{
          flex: 1,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          mt: "100px"
        }}
      >
        <Stack spacing={4} sx={{ width: "100%", maxWidth: 500 }}>
          <TextField
            label="RUT"
            variant="outlined"
            value={rut}
            onChange={(e) => setRut(formatRut(e.target.value))}
            fullWidth
          />
          <Button variant="contained" color="primary" fullWidth>
            Guardar
          </Button>
        </Stack>
      </Container>
      <Button onClick={() => navigate("/ToolList")} sx={{ mt: 2 }}>
        Ir a Herramientas
      </Button>
    </ThemeProvider>
  );
};

export default Home;