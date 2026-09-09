function PrintButton() {
  return (
    <button className="print-button no-print" onClick={() => window.print()} type="button">
      🖨️ Download / Print PDF
    </button>
  );
}

export default PrintButton;