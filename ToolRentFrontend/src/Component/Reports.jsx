import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  ThemeProvider, CssBaseline, Typography, Stack, Button,
  Container, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText,
  IconButton, Box, Paper, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, Chip
} from "@mui/material";

import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd";
import AssessmentIcon from "@mui/icons-material/Assessment";
import ContactsIcon from "@mui/icons-material/Contacts";
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import ReportIcon from '@mui/icons-material/Report';

import { createTheme } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";

import ReportsService from "../Services/ReportsServices";
import LoanToolsServices from "../Services/LoanToolsServices";
import ToolServices from "../Services/ToolServices";
import KardexServices from "../Services/KardexServices";
import ClientServices from "../Services/ClientServices";


const theme = createTheme({
  palette: {
    background: { default: "#FEF3E2" }
  }
});


const Reports = () => {

  // NAV
  const navigate = useNavigate();

  // DRAWER
  const [drawerOpen, setDrawerOpen] = useState(false);

  // FILTER DATES
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  // DATA
  const [activeLoans, setActiveLoans] = useState([]);
  const [clientsWithDelays, setClientsWithDelays] = useState([]);
  const [topTools, setTopTools] = useState([]);

  // ADMIN CHECK
  const { keycloak } = useKeycloak();
  const isAdmin = keycloak?.tokenParsed?.realm_access?.roles?.includes("ADMIN");


  // SIDEBAR LIST
  const sidebarOptions = [
    { text: "Inicio", icon: <HomeIcon />, path: "/" },
    { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
    { text: "Agregar Herramienta", icon: <LibraryAddIcon />, path: "/AddTools" },
    { text: "Ver Kardex", icon: <AssessmentIcon />, path: "/Kardex" },
    { text: "Registrar Cliente", icon: <PersonAddAltIcon />, path: "/RegisterClient" },
    { text: "Clientes", icon: <ContactsIcon />, path: "/ClientList" },
    { text: "Reportes", icon: <ReportIcon />, path: "/Reports" },
    ...(isAdmin ? [{ text: "Configuraciones", icon: <AdminPanelSettingsIcon />, path: "/Configuration" }] : [])
  ];


  // -----------------------------------------
  // REPORT LOGIC
  // -----------------------------------------

  const showActiveLoansReport = () => {
    ReportsService.getallReportsLoans()
      .then(async (response) => {

        const reports = response.data || [];

        const loans = await Promise.all(
          reports.map(async (r) => {
            try {
              const loan = await LoanToolsServices.getid(r.idLoanTool);

              const client = loan.data.clientid
                ? await ClientServices.getByid(loan.data.clientid)
                : { data: { name: "Sin cliente" } };

              const tool = loan.data.toolid
                ? await ToolServices.getid(loan.data.toolid)
                : { data: { name: "Sin herramienta" } };

              // Estado
              const today = new Date();
              const end = new Date(loan.data.finalreturndate);
              const overdue = today > end;

              return {
                date: r.date,
                client: client.data.name,
                tool: tool.data.name,
                startDate: loan.data.initiallenddate,
                endDate: loan.data.finalreturndate,
                status: overdue ? "Atrasado" : "Vigente"
              };

            } catch (e) {
              return {
                date: r.date,
                client: "Error",
                tool: "Error",
                startDate: "-",
                endDate: "-",
                status: "Error"
              };
            }
          })
        );

        setActiveLoans(loans);
      });
  };


  const showClientsLateReport = () => {
    ReportsService.getallReportsClientLate()
      .then(async (response) => {

        const reports = response.data || [];

        const clients = await Promise.all(
          reports.map(async (r) => {
            try {
              const client = await ClientServices.getByid(r.idClient);
              const tool = r.idTool
                ? await ToolServices.getid(r.idTool)
                : { data: { name: "Sin herramienta" } };

              return {
                date: r.date,
                client: client.data.name,
                email: client.data.email,
                phone: client.data.phone,
                toolName: tool.data.name
              };
            } catch {
              return {
                date: r.date,
                client: "Error",
                email: "-",
                phone: "-",
                toolName: "-"
              };
            }
          })
        );

        setClientsWithDelays(clients);
      });
  };


  const showTopToolsReport = () => {
    ReportsService.getTopToolsReport()
      .then((response) => {
        setTopTools(response.data);
        console.log("Top tools report data:", response.data);
      })
      .catch((error) => {
        console.error("Error fetching top tools report:", error);
      });
  };

  const topToolsReport = () => {
  KardexServices.getTopTools()
    .then((response) => {

      const formatted = response.data.map((row, index) => ({
        ranking: index + 1,
        name: row[0],          // nombre herramienta      // backend no lo envía
        timesRented: row[1],   // total préstamos
      }));

      setTopTools(formatted);
      console.log("Top tools formatted:", formatted);
    })
    .catch((error) => {
      console.error("Error fetching top tools:", error);
    });
};


  // GENERADORES
  const createReportLoan = () => ReportsService.createLoanReport();
  const createReportClientLate = () => ReportsService.createClientLateReport();
  const createTopToolsReport = () => ReportsService.createTopToolsReport();


  // LOAD AUTOMATIC
  useEffect(() => {
    showActiveLoansReport();
    showClientsLateReport();
    showTopToolsReport();
  }, []);


  // FORMAT
  const formatCurrency = (clp) =>
    new Intl.NumberFormat("es-CL", { style: "currency", currency: "CLP" }).format(clp);


  // -----------------------------------------
// ESTILO DE SECCIÓN
// -----------------------------------------
const sectionStyle = {
  p: 4,
  mb: 5,
  borderRadius: 4,
  backgroundColor: "#fff",
  boxShadow: "0 4px 18px rgba(255,94,0,0.15)"
};


// -----------------------------------------
// COMPONENTE HEADER DE SECCIÓN
// -----------------------------------------
const SectionHeader = ({ title, count, onGenerate }) => (
  <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
    <Typography variant="h6" sx={{ fontWeight: "bold", color: "rgba(255,94,0,1)" }}>
      {title} ({count})
    </Typography>

    <Button
      variant="contained"
      onClick={onGenerate}
      sx={{ backgroundColor: "rgba(255,94,0,1)" }}
    >
      🔄 Generar Reporte
    </Button>
  </Box>
);


  // -----------------------------------------
  // UI (ORDENADO Y BONITO)
  // -----------------------------------------
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />

      {/* BOTÓN MENU */}
      <IconButton
        onClick={() => setDrawerOpen(true)}
        sx={{
          position: "fixed", top: 16, left: 16,
          zIndex: 10, backgroundColor: "#FA812F",
          '&:hover': { backgroundColor: "#FA812F" }
        }}
      >
        <MenuIcon sx={{ color: "#fff" }} />
      </IconButton>


      <Drawer
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                variant="temporary"
                sx={{
                    [`& .MuiDrawer-paper`]: { 
                        width: 240, 
                        boxSizing: "border-box", 
                        backgroundColor: "#FEF3E2" 
                    }
                }}
            >
                <List>
                    {sidebarOptions.map((option) => (
                        <ListItem key={option.text} disablePadding>
                            <ListItemButton 
                                onClick={() => { 
                                    navigate(option.path); 
                                    setDrawerOpen(false); 
                                }}
                                sx={{
                                    '&:hover': {
                                        backgroundColor: 'rgba(255, 94, 0, 0.1)',
                                    },
                                    borderRadius: 1,
                                    mx: 1,
                                    my: 0.5
                                }}
                            >
                                <ListItemIcon sx={{ color: "#FA812F", minWidth: 40 }}>
                                    {option.icon}
                                </ListItemIcon>
                                <ListItemText 
                                    primary={option.text}
                                    sx={{
                                        '& .MuiListItemText-primary': {
                                            fontWeight: 500,
                                            fontSize: '0.95rem'
                                        }
                                    }}
                                />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Drawer>


      {/* CONTENIDO PRINCIPAL */}
      <Container sx={{ mt: 10, mb: 5 }}>

        {/* TITULO */}
        <Typography
          variant="h3"
          align="center"
          sx={{ fontWeight: "bold", color: "rgba(255,94,0,1)", mb: 4 }}
        >
          📊 Centro de Reportes
        </Typography>


        {/* ================================
            1) PRÉSTAMOS ACTIVOS
        ================================ */}
        <Paper sx={sectionStyle}>
          <SectionHeader
            title="📋 Préstamos Activos"
            count={activeLoans.length}
            onGenerate={createReportLoan}
          />

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Fecha Reporte</TableCell>
                  <TableCell>Cliente</TableCell>
                  <TableCell>Herramienta</TableCell>
                  <TableCell>Inicio</TableCell>
                  <TableCell>Fin</TableCell>
                  <TableCell>Estado</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {activeLoans.map((loan, i) => (
                  <TableRow key={i}>
                    <TableCell>{loan.date}</TableCell>
                    <TableCell>{loan.client}</TableCell>
                    <TableCell>{loan.tool}</TableCell>
                    <TableCell>{loan.startDate}</TableCell>
                    <TableCell>{loan.endDate}</TableCell>
                    <TableCell>
                      <Chip
                        label={loan.status}
                        sx={{
                          backgroundColor:
                            loan.status === "Atrasado" ? "#ffe5e5" : "#e8f5e9",
                          color:
                            loan.status === "Atrasado" ? "#c62828" : "#2e7d32",
                          fontWeight: "bold"
                        }}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>

            </Table>
          </TableContainer>
        </Paper>



        {/* ================================
            2) CLIENTES CON ATRASO
        ================================ */}
        <Paper sx={sectionStyle}>
          <SectionHeader
            title="⚠ Clientes con Atraso"
            count={clientsWithDelays.length}
            onGenerate={createReportClientLate}
          />

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Fecha Reporte</TableCell>
                  <TableCell>Cliente</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Teléfono</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {clientsWithDelays.map((c, i) => (
                  <TableRow key={i}>
                    <TableCell>{c.date}</TableCell>
                    <TableCell>{c.client}</TableCell>
                    <TableCell>{c.email}</TableCell>
                    <TableCell>{c.phone}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>


        {/* ================================
            3) TOP HERRAMIENTAS
        ================================ */}
        <Paper sx={sectionStyle}>
          <SectionHeader
            title="🏆 Herramientas Más Prestadas"
            count={topTools.length}
            onGenerate={createTopToolsReport}
          />

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Ranking</TableCell>
                  <TableCell>Herramienta</TableCell>
                  <TableCell>Categoría</TableCell>
                  <TableCell>Prestada</TableCell>
                  <TableCell>Ingresos</TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {topTools.map((t) => (
                  <TableRow key={t.id}>
                    <TableCell>#{t.ranking}</TableCell>
                    <TableCell>{t.name}</TableCell>
                    <TableCell>{t.category}</TableCell>
                    <TableCell>{t.timesRented}</TableCell>
                    <TableCell sx={{ fontWeight: "bold", color: "green" }}>
                      {formatCurrency(t.revenue)}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>

            </Table>
          </TableContainer>
        </Paper>


      </Container>
    </ThemeProvider>
  );
};


export default Reports;
