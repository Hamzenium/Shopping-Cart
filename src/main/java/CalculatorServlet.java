import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

@WebServlet("/CalculatorServlet")
public class CalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public CalculatorServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String noItemsParam = request.getParameter("noItems");
        String priceParam = request.getParameter("price");
        String taxParam = request.getParameter("tax");

        // Check if parameters are missing
        if (noItemsParam == null || priceParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        HttpSession session = request.getSession();
        double tax;

        // If tax parameter is provided, parse it and store it in session
        if (taxParam != null && !taxParam.isEmpty()) {
            try {
                tax = Double.parseDouble(taxParam);
                session.setAttribute("taxRate", tax);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tax rate format");
                return;
            }
        } else {
            // If tax rate parameter is not provided, check if there is a stored tax rate in session
            Double sessionTax = (Double) session.getAttribute("taxRate");
            if (sessionTax != null) {
                tax = sessionTax;
            } else {
                // If there's no stored tax rate in session, get default tax rate from ServletContext
                ServletContext servletContext = getServletContext();
                String defaultTaxRate = servletContext.getInitParameter("defaultTaxRate");
                try {
                    tax = Double.parseDouble(defaultTaxRate);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid default tax rate");
                    return;
                }
            }
        }

        try {
            int noItems = Integer.parseInt(noItemsParam);
            double price = Double.parseDouble(priceParam);

            double total = noItems * price * (1 + tax / 100);
            total = Math.round(total * 100) / 100.0;

            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Shopping cart Price Calculator</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Shopping cart Price Calculator</h1>");
            out.println("<p>You entered:</p>");
            out.println("<ul>");
            out.println("<li>Number of items: " + noItems + "</li>");
            out.println("<li>Price of each item: $" + price + "</li>");
            out.println("<li>Tax rate: " + tax + "%</li>");
            out.println("</ul>");
            out.println("<p>The total price is calculated as:</p>");
            out.println("<p>total = noItems * price * (1 + tax / 100)</p>");
            out.println("<p>The total price is: $" + total + "</p>");
            out.println("</body>");
            out.println("</html>");
            response.setContentType("text/html");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}


