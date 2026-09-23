function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        type="button"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
      >
        ← Previous
      </button>

      <span>Page {page + 1} of {totalPages}</span>

      <button
        type="button"
        onClick={() => onPageChange(page + 1)}
        disabled={page + 1 >= totalPages}
      >
        Next →
      </button>
    </div>
  );
}

export default Pagination;