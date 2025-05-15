# Job-Crawler

**Job-Crawler** is a Java desktop application that aggregates job postings from three major Korean job search websites: Incruit, JobKorea, and Saramin. It provides a graphical user interface (GUI) that allows users to search for web developer positions and view job listings from all three sites in a single window.

## Features

- **Tabbed Interface:** The application displays results from Incruit, JobKorea, and Saramin in separate tabs for easy comparison.
- **Automated Search:** Uses Selenium (with HtmlUnitDriver) to perform automated searches for "웹 개발" (web development) jobs on each site.
- **Web Scraping:** Utilizes JSoup to parse and extract job postings and relevant details from the search results pages.
- **Interactive Table:** Job postings are displayed in tables, with company names, job titles, and other relevant information.
- **Clickable Links:** Each job posting includes a button that opens the job listing in the user's default web browser.
- **Custom Table Components:** Includes custom button renderers and editors for interactive table cells.

## How It Works

1. **Startup:** The main class (`Crawler.java`) launches a GUI window with three tabs, one for each job site.
2. **Search:** For each site, a dedicated crawler class (`C_JSoupCrawlerIncruit`, `C_JSoupCrawlerJobkorea`, `C_JSoupCrawlerSaramIn`) uses Selenium to perform a search and JSoup to scrape the results.
3. **Display:** Results are shown in tables with clickable links to the original job postings.

## Requirements

- Java (JDK 8 or higher recommended)
- Selenium (with HtmlUnitDriver)
- JSoup
- SwingX (for JXTable)
- chromedriver.exe (must be available in the project directory for Selenium, though HtmlUnitDriver is used by default)

## Usage

1. Build and run the `Crawler` class.
2. The application window will open, showing tabs for Incruit, JobKorea, and Saramin.
3. Browse job postings and click the link buttons to view details in your browser.

## Demo

See the program in action on YouTube: [Job-Crawler Demo](https://youtu.be/sGvSVWuud5U?si=N6uEqQipM4raKotz&t=221)
