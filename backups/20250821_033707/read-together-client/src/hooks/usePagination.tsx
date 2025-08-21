import { useState, useMemo } from 'react';

interface UsePaginationProps<T> {
  data: T[];
  itemsPerPage: number;
}

interface UsePaginationReturn<T> {
  currentPage: number;
  totalPages: number;
  currentData: T[];
  goToPage: (page: number) => void;
  goToNextPage: () => void;
  goToPrevPage: () => void;
  hasNextPage: boolean;
  hasPrevPage: boolean;
  startIndex: number;
  endIndex: number;
  totalItems: number;
}

export function usePagination<T>({ data, itemsPerPage }: UsePaginationProps<T>): UsePaginationReturn<T> {
  const [currentPage, setCurrentPage] = useState(1);

  const paginationData = useMemo(() => {
    const totalItems = data.length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, totalItems);
    const currentData = data.slice(startIndex, endIndex);

    return {
      totalPages,
      currentData,
      startIndex: startIndex + 1, // +1 for human-readable index
      endIndex,
      totalItems,
      hasNextPage: currentPage < totalPages,
      hasPrevPage: currentPage > 1,
    };
  }, [data, itemsPerPage, currentPage]);

  const goToPage = (page: number) => {
    if (page >= 1 && page <= paginationData.totalPages) {
      setCurrentPage(page);
    }
  };

  const goToNextPage = () => {
    if (paginationData.hasNextPage) {
      setCurrentPage(prev => prev + 1);
    }
  };

  const goToPrevPage = () => {
    if (paginationData.hasPrevPage) {
      setCurrentPage(prev => prev - 1);
    }
  };

  // Reset to page 1 when data changes
  const resetPage = () => setCurrentPage(1);

  // Auto-reset if current page exceeds total pages
  if (currentPage > paginationData.totalPages && paginationData.totalPages > 0) {
    setCurrentPage(1);
  }

  return {
    currentPage,
    totalPages: paginationData.totalPages,
    currentData: paginationData.currentData,
    goToPage,
    goToNextPage,
    goToPrevPage,
    hasNextPage: paginationData.hasNextPage,
    hasPrevPage: paginationData.hasPrevPage,
    startIndex: paginationData.startIndex,
    endIndex: paginationData.endIndex,
    totalItems: paginationData.totalItems,
  };
}