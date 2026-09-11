import type { Category } from "../types";

type Props = {
  categories: Category[];
  onSelectCategory: (categoryId: string) => void;
};

export function CategoriesScreen({ categories, onSelectCategory }: Props) {
  return (
    <div className="page-shell">
      <div className="section-header">
        <h2>Categorías</h2>
      </div>

      <div className="category-gallery">
        {categories.map((category) => (
          <button
            key={category.id}
            className="category-hero-card"
            onClick={() => onSelectCategory(category.id)}
            style={{ background: `linear-gradient(135deg, ${category.color || "#f9d6a5"}, #ffffff)` }}
          >
            <span className="category-icon big" style={{ background: category.color || "#f7d59c" }}>
              {category.name.slice(0, 1).toUpperCase()}
            </span>
            <div>
              <strong>{category.name}</strong>
              <small>Explorar productos</small>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
